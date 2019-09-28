package men.brakh.googleDriveBot.driveClient

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import men.brakh.googleDriveBot.vkbot.LongPollListener
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Files

class GoogleDriveClientImpl(applicationName: String) : GoogleDriveClient {

    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val tokensDirectoryPath = "tokens"
    private val scopes = DriveScopes.all()
    private val credentialsFileNamePath = "/credentials.json"
    private val service: Drive

    private val logger = LoggerFactory.getLogger(LongPollListener::class.java)



    init {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val service = Drive.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
            .setApplicationName(applicationName)
            .build()
        this.service = service
    }

    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val credentials = GoogleDriveClientImpl::class.java.getResourceAsStream(credentialsFileNamePath)
            ?: throw FileNotFoundException("Resource not found: $credentialsFileNamePath")
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(credentials))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, jsonFactory, clientSecrets, scopes
        )
            .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    override fun getFiles(parentFolderId: String?): List<GoogleDriveFile> {
        val request = service.files()
            .list()

        if (parentFolderId != null) request.q = "'$parentFolderId' in parents"

        return request
            .execute()
            .files
    }

    override fun createFolder(parentFolderId: String?, folderName: String): GoogleDriveFile {
        val fileMetadata = GoogleDriveFile()
        fileMetadata.name = folderName
        if (parentFolderId != null) {
            fileMetadata.parents = listOf(parentFolderId)
        }
        fileMetadata.mimeType = "application/vnd.google-apps.folder"

        logger.info("Creation new folder $folderName in folder with id $parentFolderId")

        return service.files().create(fileMetadata)
            .execute()
    }

    override fun uploadFile(folderId: String?, file: File): GoogleDriveFile {
        val fileMetadata = GoogleDriveFile()
        fileMetadata.name = file.name
        fileMetadata.parents = listOf(folderId)

        val path = file.toPath()
        val mimeType = Files.probeContentType(path)

        val mediaContent = FileContent(mimeType, file)

        logger.info("Uploading new file ${file.name} in folder with id $folderId")

        return upload(fileMetadata, mediaContent)
    }

    override fun uploadFile(
        folderId: String?,
        fileName: String,
        mimeType: String,
        inputStream: InputStream
    ): GoogleDriveFile {
        val fileMetadata = GoogleDriveFile()
        fileMetadata.name = fileName
        fileMetadata.parents = listOf(folderId)

        val mediaContent =  InputStreamContent(mimeType, inputStream)

        logger.info("Uploading new file $fileName in folder with id $folderId")

        return upload(fileMetadata, mediaContent)
    }

    private fun upload (
        fileMetadata: GoogleDriveFile,
        mediaContent: AbstractInputStreamContent
    ): GoogleDriveFile {

        return service.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()
    }

    override fun getFile(fileId: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        service.files().get(fileId)
            .executeMediaAndDownloadTo(outputStream)

        return outputStream.toByteArray()
    }

    override fun getByFilenameOrNull(parentFolderId: String?, filename: String): GoogleDriveFile? {

        return getFiles(parentFolderId)
            .find { file -> file.name == filename }

    }
}
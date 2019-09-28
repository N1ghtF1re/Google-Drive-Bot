package men.brakh.googleDriveBot.driveClient

import java.io.File
import java.io.InputStream


typealias GoogleDriveFile = com.google.api.services.drive.model.File


interface GoogleDriveClient {
    fun getByFilenameOrNull(parentFolderId: String?, filename: String): GoogleDriveFile?
    fun getFiles(parentFolderId: String? = null): List<GoogleDriveFile>
    fun createFolder(parentFolderId: String? = null, folderName: String): GoogleDriveFile
    fun uploadFile(folderId: String? = null, file: File): GoogleDriveFile
    fun uploadFile(
        folderId: String? = null, fileName: String, mimeType: String,
        inputStream: InputStream
    ): GoogleDriveFile
    fun getFile(fileId: String): ByteArray
}
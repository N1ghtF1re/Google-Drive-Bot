<h1 align="center">Google Drive Bot</h1>
<p align="center"><img src="https://www.internetandtechnologylaw.com/files/2019/06/iStock-872962368-chat-bots.jpg" width="150"></p>

<p align="center">
<a href="https://github.com/N1ghtF1re/Google-Drive-Bot/stargazers"><img src="https://img.shields.io/github/stars/N1ghtF1re/Google-Drive-Bot.svg" alt="Stars"></a>
<a href="https://github.com/N1ghtF1re/Google-Drive-Bot/releases"><img src="https://img.shields.io/badge/download-brightgreen.svg" alt="Download"></a>
<a href="https://github.com/N1ghtF1re/Google-Drive-Bot/releases"><img src="https://img.shields.io/github/tag/N1ghtF1re/Google-Drive-Bot.svg" alt="Latest Stable Version"></a>
<a href="https://github.com/N1ghtF1re/Google-Drive-Bot/blob/master/LICENSE"><img src="https://img.shields.io/github/license/N1ghtF1re/Google-Drive-Bot.svg" alt="License"></a>

</p>
</p>

## About
VK Bot which can upload file to google drive


## Used technologies
The bot written in Java using VK Java SDK, Goole Drive API v3

## How to run
1. Go to https://developers.google.com/drive/api/v3/quickstart/java
2. Click "Enable the Drive API"
3. Download crenditals 
4. Place it in ```src/main/resources```
5. Fill the ```src/main/resources/application.properties``` file
6. Run ```gradle clean build``` and then ```java -jar builds/lib/gd-googleDriveBot-1.0.jar```
7. Follow the instructions

Note: you have to create and configure vk group as bot before (enable longpolling, create token, allow to invite to conversations)

## How to use 
Bot will upload files if you attach it and place hash tag. 

Also, bot can upload text. For example, after message 

``` 
#test 
Some test message for test
```

The bot will create text file in folder TEST and place message "Some test message for test" there 

It's also possible to define filename. For example, after message

``` 
#test_filename
Some test message for test
```

The bot will create text file "filename" in folder TEST and place message "Some test message for test" there 

Note: the bot will add creation time in filename automaticaly. 

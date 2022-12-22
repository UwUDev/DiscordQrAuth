# DiscordQrAuth
Fully reversed discord QR code auth system using websocket without using shitty chromedriver

# How to use
## Generate QR code
```java
DiscordQrAuth auth = new DiscordQrAuth();
auth.start();

String fingerprint = auth.getFingerprint(); // qr code fingerprint
String qrCode = auth.getQrUrl(); // qr code url, you have to turn it into a qr code
String qrCodeImageUrl = auth.getQrImageUrl(); // qr code image url, you can use it directly
```

## Get user token
```java
String token auth.awaitToken(); // wait for user to scan the qr code and accept
```

## Get scanned user
You can only use this method after you got the user token
```java
PendingUser user = auth.getUser(); // get scanned user
```
**Note**: the `PendingUser` class contains the user information prompt by discord when user scan the qr code. If you want to get all the user information, you have to use the token to request user profile to the discord api.

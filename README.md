# cm2
**Kotlin** command line application to handle CodeMarathon 2.0.

## Start Development
1. Install latest [IntelliJ IDEA Community Edition (2017.2.6)](https://www.jetbrains.com/idea/download/).
2. Clone cm2 repo to your computer, type `clone git@github.com:evgenii-kanivets/cm2.git` in your command line.
3. Open the project through IntelliJ IDEA.
4. Run 'MainKt', there are must to be errors in console. This is because of credentials you need to access Firebase Database and Telegram Chat.
5. Contact `Evgenii Kanivets` to get credentials.

## Insert credentials
1. Place <creds>.json into project folder.
2. Update `FirebaseClient.kt` to locate your credentials:
  ```
  init {
        val serviceAccount = FileInputStream("codemarathon-2-dev-firebase-adminsdk.json")

        val options = FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://codemarathon-2-dev.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
    }
  ```
3. Update `TelegramBot.kt` with your bot access token:
```
private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.telegram<token>.org/bot/") // Insert Telegram Bot token here
                .build()
    }
```

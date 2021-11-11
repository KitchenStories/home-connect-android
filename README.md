# Work in Progress: HomeConnect Api wrapper for Android

## Sample app

To switch between real and simulator environment, simply comment in/out the respective `baseUrl` in `MainActivity.kt`

For the sample app, copy your cliendId and cliendSecret from developer.home-connect.com

```
clientId="<insert-your-clientId>"
clientSecret="<insert-your-clientSecred>"
```

## How to use

### Home Connect Client

You can make API requests through the `HomeConnectClient` interface. To do this, you should keep a single instance of `DefaultHomeConnectClient` alive through the lifecycle of your app.
In order to construct the instance, you need to provide a base url for the client (usually either https://api.home-connect.com/ or https://simulator.home-connect.com/), your client credentials and an object that implements the `HomeConnectSecretsStore` interface.
Here you have to provide a mechanism (SharedPreferences, Jetpack DataStore or something else) to store the user's access token. Clearing this access token has the consequence of logging the user out.
Example usage with Dagger:

```
@Module
class HomeConnectModule {

    @Provides
    @Singleton
    fun provideHomeConnectSecretsStore(context: Context): HomeConnectSecretsStore =
            MyHomeConnectSecretsStore(context)

    @Provides
    @Singleton
    fun provideHomeConnectClient(secretsStore: HomeConnectSecretsStore): HomeConnectClient =
            DefaultHomeConnectClient(
                baseUrl = "https://api.home-connect.com/",
                clientCredentials = HomeConnectClientCredentials(
                    clientId = "<insert-your-client-id>",
                    clientSecret = "<insert-your-client-secret>",
                ),
                homeConnectSecretsStore = secretsStore,
            )

}
```

Add the HomeConnectModule to your AppComponent and then you can inject the client wherever you need it.

### Authorization

There is a helper class called `HomeConnectAuthorization` for managing the initial authorization flow where the user has to provide his Home Connect credentials.
Example usage with [Lifecycle from architecture components](https://developer.android.com/topic/libraries/architecture/coroutines):

```
class HomeConnectAuthorizationFragment : Fragment(R.layout.fragment_home_connect_authorization) {

    private var homeConnectAuthorization: HomeConnectAuthorization? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.web_view)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                homeConnectAuthorization = HomeConnectAuthorization()
                homeConnectAuthorization.authorize(webView, savedInstanceState, onRequestAccessTokenStarted = ::showLoadingIndicator)
                hideLoadingIndicator()
                // user is authorized now
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val webView = requireView().findViewById<WebView>(R.id.web_view)
        homeConnectAuthorization?.saveInstanceState(webView, outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeConnectAuthorization = null
    }

}

```

Before starting the authorization flow, you should check with `HomeConnectClient.isAuthorized` whether the user is authorized already.

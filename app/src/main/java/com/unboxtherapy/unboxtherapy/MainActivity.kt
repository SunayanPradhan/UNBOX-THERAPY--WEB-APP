package com.unboxtherapy.unboxtherapy

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    lateinit var refresh: SwipeRefreshLayout

    lateinit var webView:WebView

    lateinit var noInternet: LottieAnimationView

    private lateinit var connectivityObserver: ConnectivityObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refresh=findViewById(R.id.refresh)

        webView=findViewById(R.id.web_view)

        noInternet=findViewById(R.id.no_internet)


        connectivityObserver=NetworkConnectivityObserver(applicationContext)

        webView.webViewClient=object :WebViewClient(){

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, url)

                view?.loadUrl(url!!)

                return true

            }


        }

        val webSettings=webView.settings

        webSettings.javaScriptEnabled=true

        webView.loadUrl("https://unboxtherapy.in/")

        refresh.setOnRefreshListener {
            webView.reload()

            refresh.isRefreshing=false


        }

        if (isNetworkAvailable(this)){


            refresh.visibility= View.VISIBLE

            noInternet.visibility=View.GONE


        }

        else{

            refresh.visibility= View.GONE

            noInternet.visibility=View.VISIBLE


        }

        connectivityObserver.observe().onEach {

            if (it==ConnectivityObserver.Status.Available){

                refresh.visibility=View.VISIBLE

                noInternet.visibility=View.GONE

            }

            else{


                refresh.visibility= View.GONE

                noInternet.visibility=View.VISIBLE


            }


        }.launchIn(lifecycleScope)








    }


    private fun isNetworkAvailable(context: Context?):Boolean {

        if (context == null) return false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {

                when {

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {

                        return true

                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {


                        return true

                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {

                        return true

                    }


                }

            }


        } else {

            val activeNetworkInfo = connectivityManager.activeNetworkInfo

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {

                return true

            }


        }

        return false

    }

    private fun backAlert(){

        val dialogView=View.inflate(this,R.layout.pop_up_dialog_layout,null)


        val builder=AlertDialog.Builder(this).setView(dialogView).create()

        builder.show()

        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val dialogYes=dialogView.findViewById<Button>(R.id.dialog_yes)

        val dialogNo=dialogView.findViewById<Button>(R.id.dialog_no)

        val dialogTxt=dialogView.findViewById<TextView>(R.id.dialog_txt)

        dialogTxt.text="DO YOU WANT TO EXIT?"

        dialogNo.setOnClickListener {

            builder.dismiss()

        }

        dialogYes.setOnClickListener {

            finishAffinity()


        }




    }


    override fun onBackPressed() {

        if (noInternet.isVisible){

            finishAffinity()

        }
        else{

            if (webView.canGoBack()){

                webView.goBack()

            }
            else{


                backAlert()

            }



        }



    }


}
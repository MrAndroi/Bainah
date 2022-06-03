package com.yarmouk.bainah.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.yarmouk.bainah.R
import com.yarmouk.bainah.models.Message
import com.yarmouk.bainah.models.Notification
import com.yarmouk.bainah.models.Request
import com.yarmouk.bainah.repo.Repository
import com.yarmouk.bainah.services.CHANNEL_ID
import com.yarmouk.bainah.ui.activities.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltWorker
class UploadReportWorker @AssistedInject
constructor(@Assisted context: Context,@Assisted params: WorkerParameters,private val repo: Repository):CoroutineWorker(context,params) {

    lateinit var storageReference: StorageReference
    var imageRef = FirebaseStorage.getInstance().reference

    override suspend fun doWork(): Result {
        storageReference = FirebaseStorage.getInstance().getReference("photos")
        val imagesPaths = inputData.getStringArray("list") as Array<String>
        val report = inputData.getString("report") as String
        val request = Gson().fromJson(report,Request::class.java)

        val list = arrayListOf<Uri>()
        imagesPaths.forEach {
            list.add(it.toUri())
        }
        uploadImagesToFireBase(list,repo, request)
        return Result.success()
    }

    private suspend fun uploadImagesToFireBase(uriList:ArrayList<Uri>,repo: Repository,request: Request){
        val images = arrayListOf<String>()
        var numberOfImages = 0
        makeStatusNotification(
            applicationContext.resources.getString(R.string.uploading_will_start),
            applicationContext.resources.getString(R.string.uploading_your_request),
            applicationContext,
            progress = true
        )
        for (i in uriList) {
            imageRef.child("Photos").child(i.lastPathSegment.toString()).putFile(i)
                .addOnProgressListener {
                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                        if(it.isComplete){
                            images.add(it.result.toString())
                            numberOfImages++
                            makeStatusNotification(
                                applicationContext.resources.getString(R.string.uploading_image_number) +" "+ numberOfImages,
                                applicationContext.resources.getString(R.string.uploading_images),
                                applicationContext,
                                progress = true
                            )
                            if(numberOfImages == uriList.size){
                                request.accidentImages = images.toTypedArray().toList()
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val response = repo.postNewRequest(request)
                                        if (response.isSuccessful) {
                                            makeStatusNotification(
                                                applicationContext.resources.getString(R.string.your_request_has_sent),
                                                applicationContext.resources.getString(R.string.request_sent),
                                                applicationContext,
                                                response.body()?.requestID
                                            )
                                        }
                                        else{
                                            makeStatusNotification(
                                                applicationContext.resources.getString(R.string.error_while_uploading),
                                                applicationContext.resources.getString(R.string.sorry_error_happen),
                                                applicationContext)
                                        }
                                    } catch (e: Exception) {
                                        makeStatusNotification(
                                            applicationContext.resources.getString(R.string.error_while_uploading),
                                            applicationContext.resources.getString(R.string.sorry_error_happen),
                                            applicationContext)
                                    }
                                }
                            }

                        }
                    }

                }.addOnCompleteListener {
                }.addOnFailureListener {
                    makeStatusNotification(
                        applicationContext.resources.getString(R.string.error_while_uploading),
                        applicationContext.resources.getString(R.string.sorry_error_happen),
                        applicationContext)
                }

        }
    }

    private fun makeStatusNotification(message: String,title:String, context: Context,responseCode:String?=null,progress:Boolean = false) {
        val bundle = bundleOf("response" to responseCode)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.qrCodeFragment)
            .setArguments(bundle)
            .createPendingIntent()

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "VERBOSE_NOTIFICATION_CHANNEL_NAME"
            val description = "VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        if(progress){
            // Create the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setProgress(0,100,true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(0,500))

            // Show the notification
            NotificationManagerCompat.from(context).notify(1, builder.build())
        }
        else{
            // Create the notification
            if(responseCode != null) {
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message)
                    )
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(longArrayOf(0,500,1000,1000))

                // Show the notification
                NotificationManagerCompat.from(context).notify(1, builder.build())
            }

            else{
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message)
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(LongArray(0))

                // Show the notification
                NotificationManagerCompat.from(context).notify(1, builder.build())
            }


        }

    }
}
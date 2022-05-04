package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.yarmouk.bainah.R
import com.yarmouk.bainah.adapter.ImagePreviewAdapter
import com.yarmouk.bainah.models.Request
import com.yarmouk.bainah.others.TakeImagesInstructionsDialog
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.Status
import com.yarmouk.bainah.util.mutation
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.android.synthetic.main.info_four_fragment.*
import kotlinx.android.synthetic.main.submit_report_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class InfoFourFragment:Fragment(R.layout.info_four_fragment)  {

    private lateinit var imagesPaths:ArrayList<Uri>
    private lateinit var finalImagesPaths:MutableList<String>
    private lateinit var imagesAdapter: ImagePreviewAdapter
    private lateinit var uploadImagesDialog: AlertDialog
    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private var showImages:Boolean = true

    private var report: Request = Request()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagesPaths = ArrayList()
        finalImagesPaths = ArrayList()
        imagesAdapter = ImagePreviewAdapter{image,_ ->
            imagesPaths.remove(image)
            mainViewModel.updateImages(imagesPaths)
        }

        uploadImagesDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.uploading_images))
            .setCancelable(false)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImagesBtn.setOnClickListener {
            TedImagePicker.with(requireContext())
                .max(15,getString(R.string.max_images_msg))
                .min(5,getString(R.string.min_images_msg))
                .buttonText(getString(R.string.done))
                .buttonBackground(R.drawable.btn_done)
                .cameraTileBackground(R.color.primaryColor)
                .title(getString(R.string.please_select))
                .startMultiImage { uriList ->
                    for (i in uriList){
                        if(imagesPaths.contains(i)){
                            continue
                        }
                        else{
                            imagesPaths.add(i)
                        }
                    }
                    mainViewModel.updateImages(imagesPaths)
                }
        }
        setUpImagesRecyclerView()
        setUpObservers()

        next4.setOnClickListener {
            if(finalImagesPaths.size >= 5){
                requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem+1
            }
            else {
                if(imagesPaths.size >= 5) {
                    requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem+1
                }
                else{
                    showSnackBar(getString(R.string.please_select),R.color.red)
                }
            }
        }

        back3.setOnClickListener {
            requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem-1
        }

        btnHelp.setOnClickListener {
            TakeImagesInstructionsDialog()
                .apply {
                    isCancelable = false
                }
                .show(requireParentFragment().childFragmentManager,"TakeImagesInstructionsDialog")
        }
    }

    private fun setUpImagesRecyclerView(){
        rvImages.setHasFixedSize(false)
        rvImages.adapter = imagesAdapter
    }

    private fun setUpObservers(){
         mainViewModel.request.observe(viewLifecycleOwner){
             report = it
        }
        mainViewModel.imagesPath.observe(viewLifecycleOwner){
            if(it.size <= 0){
                tvTotalImages.text = getString(R.string.please_select)
                selectImagesBtn.text = getString(R.string.upload_images)
                ivTakeImage.isVisible = true
                rvImages.isVisible = false
            }
            else{
                tvTotalImages.text = "${it.size}/15"
                selectImagesBtn.text = getString(R.string.add_more)
                imagesAdapter.differ.submitList(it)
                imagesAdapter.notifyDataSetChanged()
                ivTakeImage.isVisible = false
                rvImages.isVisible = true

            }
        }

        mainViewModel.imagesResponse.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS ->{
                    uploadImagesDialog.dismiss()
                    finalImagesPaths =  it.data?.imagesPaths?.toMutableList()!!
                    mainViewModel.request.mutation { request ->
                        request.value?.accidentImages = finalImagesPaths.toTypedArray().toList()
                    }

                }
                Status.ERROR ->{
                    uploadImagesDialog.dismiss()
                    showSnackBar("${it.message}",R.color.red)
                }
                Status.LOADING ->{
                    CoroutineScope(Dispatchers.Main).launch {
                        uploadImagesDialog.show()
                    }
                }
            }
        }
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

    private suspend fun showImageInstructions(){
        delay(500)
        TakeImagesInstructionsDialog()
            .apply {
                isCancelable = false
            }
            .show(requireParentFragment().childFragmentManager,"TakeImagesInstructionsDialog")
        showImages = false
    }

    override fun onResume() {
        super.onResume()
        if(showImages){
            lifecycleScope.launchWhenResumed {
                showImageInstructions()
            }
        }
    }
}
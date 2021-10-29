package io.stipop.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.stipop.Config
import io.stipop.Constants
import io.stipop.R
import io.stipop.adapter.GridStickerAdapter
import io.stipop.base.Injection
import io.stipop.databinding.FragmentStickerPackageBinding
import io.stipop.event.PackageDownloadEvent
import io.stipop.models.StickerPackage
import io.stipop.view.viewmodel.PackageDetailViewModel
import kotlinx.coroutines.launch

class PackageDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentStickerPackageBinding? = null
    private lateinit var viewModel: PackageDetailViewModel
    private val adapter: GridStickerAdapter by lazy { GridStickerAdapter() }

    companion object {
        fun newInstance(packageId: Int, entrancePoint: String) =
            PackageDetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.IntentKey.PACKAGE_ID, packageId)
                    putString(Constants.IntentKey.ENTRANCE_POINT, entrancePoint)
                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog: BottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = getBottomSheetDialogDefaultHeight()
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheet.layoutParams.height =
                        behavior.peekHeight
                    BottomSheetBehavior.STATE_COLLAPSED -> bottomSheet.layoutParams.height =
                        behavior.peekHeight
                    BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                    else -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 90 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerPackageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this)).get(
            PackageDetailViewModel::class.java
        )
        viewModel.stickerPackage.observeForever {
            updateUi(it)
            adapter.updateData(it)
        }
        PackageDownloadEvent.liveData.observe(viewLifecycleOwner) {
            binding?.run {
                downloadTV.text = getString(R.string.downloaded)
                downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
            }
        }
        applyTheme()
        arguments?.let {
            val packageId = it.getInt(Constants.IntentKey.PACKAGE_ID, -1)
            val entrancePoint = it.getString(Constants.IntentKey.ENTRANCE_POINT)
            val gridLayoutManager = GridLayoutManager(requireContext(), Config.detailNumOfColumns)
            binding?.recyclerView?.layoutManager = gridLayoutManager
            binding?.recyclerView?.adapter = adapter
            binding
            lifecycleScope.launch {
                viewModel.trackViewPackage(packageId, entrancePoint)
                viewModel.loadsPackages(packageId)
            }
        } ?: run {
            dismiss()
        }

        binding?.run {
            backIV.setOnClickListener {
                dismiss()
            }
            closeIV.setOnClickListener {
                dismiss()
            }
            downloadTV.setOnClickListener {
                viewModel.requestDownloadPackage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun applyTheme() {
        binding?.run {
            val drawable = containerLL.background as GradientDrawable
            drawable.setColor(Color.parseColor(Config.themeGroupedContentBackgroundColor)) // solid  color
            contentsRL.setBackgroundColor(Color.parseColor(Config.themeBackgroundColor))
            packageNameTV.setTextColor(Config.getDetailPackageNameTextColor(requireContext()))
            backIV.setImageResource(Config.getBackIconResourceId(requireContext()))
            closeIV.setImageResource(Config.getCloseIconResourceId(requireContext()))
            backIV.setIconDefaultsColor()
            closeIV.setIconDefaultsColor()
        }
    }

    private fun updateUi(stickerPackage: StickerPackage) {
        binding?.run {
            Glide.with(requireContext()).load(stickerPackage.packageImg).into(packageIV)
            packageNameTV.text = stickerPackage.packageName
            artistNameTV.text = stickerPackage.artistName
            if (stickerPackage.isDownloaded()) {
                downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background_disable)
                downloadTV.text = getString(R.string.downloaded)
            } else {
                downloadTV.setBackgroundResource(R.drawable.detail_download_btn_background)
                downloadTV.text = getString(R.string.download)
                val drawable2 = downloadTV.background as GradientDrawable
                drawable2.setColor(Color.parseColor(Config.themeMainColor)) // solid  color
            }
            downloadTV.tag = stickerPackage.isDownloaded()
        }
    }
}
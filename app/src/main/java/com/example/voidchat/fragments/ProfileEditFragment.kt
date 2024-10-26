package com.example.voidchat.fragments

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.example.voidchat.data.DBNODES
import com.example.voidchat.data.User
import com.example.voidchat.databinding.FragmentProfileEditBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class ProfileEditFragment : Fragment() {
	private lateinit var binding: FragmentProfileEditBinding
	private lateinit var userDB: DatabaseReference
	private var userId: String = ""
	private lateinit var userProfileUri: Uri
	private lateinit var userStorage: StorageReference
	private var isProfileClicked = false
	private var imageLink: String = "no Link"


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentProfileEditBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		userDB = FirebaseDatabase.getInstance().reference
		userStorage = FirebaseStorage.getInstance().reference

		arguments?.getString("id", "")?.let {
			userId = it
			getUserById(it)
		}

		binding.btnSave.setOnClickListener {
			if (isProfileClicked) {
				uploadProfileImage(userProfileUri)
			}
			var userMap: MutableMap<String, Any> = mutableMapOf()
			userMap["fullName"] = binding.fullNameET.text.toString().trim()
			userMap["bio"] = binding.bioET.text.toString().trim()
			userDB.child(DBNODES.USER).child(userId).updateChildren(userMap).addOnCompleteListener {
				if (it.isSuccessful) {
					Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT)
						.show()
				} else {
					Toast.makeText(requireContext(), "${it.exception?.message}", Toast.LENGTH_SHORT)
						.show()
				}
			}
			requireActivity().onBackPressedDispatcher.onBackPressed()
		}
		binding.profileImageIV.setOnClickListener {


				requestPermission()

				isProfileClicked = true
				pickProfileImage()


		}
	}


	private fun getUserById(userId: String) {
		userDB.child(DBNODES.USER).child(userId).addValueEventListener(
			object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					snapshot.getValue(User::class.java)?.let {
						binding.apply {
							fullNameET.setText(it.fullName)
							bioET.setText(it.bio)
							if (it.profilePicture != "no Link" && it.profilePicture != "") {
								profileImageIV.load(it.profilePicture)
							}
						}
					}
				}

				override fun onCancelled(error: DatabaseError) {
					Toast.makeText(
						requireContext(),
						"Database Error: ${error.message}",
						Toast.LENGTH_SHORT
					)
						.show()
				}
			}
		)
	}


	private fun uploadProfileImage(userProfileUri: Uri) {

		var profileStorage = userStorage.child(DBNODES.UPLOAD).child(userId).child("profile.jpg")
		profileStorage.putFile(userProfileUri).addOnCompleteListener {
			if (it.isSuccessful) {
				profileStorage.downloadUrl.addOnSuccessListener { data ->
					imageLink = data.toString()
					profileUpdateWithImageLink(imageLink)
					Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	private fun profileUpdateWithImageLink(imageLink: String) {

		var userMap: MutableMap<String, Any> = mutableMapOf()

		userMap["fullName"] = binding.fullNameET.text.toString().trim()
		userMap["bio"] = binding.bioET.text.toString().trim()
		userMap["profilePicture"] = imageLink

		userDB.child(DBNODES.USER).child(userId).updateChildren(userMap)
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT)
						.show()
				} else {
					Toast.makeText(
						requireContext(),
						"${task.exception?.message}",
						Toast.LENGTH_SHORT
					)
						.show()
				}

			}


	}


	private fun pickProfileImage() {

		ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
			.createIntent { intent ->
				startForProfileImageResult.launch(intent)
			}
	}

	private val startForProfileImageResult =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
			val resultCode = result.resultCode
			val data = result.data

			when (resultCode) {
				Activity.RESULT_OK -> {
					data?.data?.let {

						userProfileUri = it
						binding.profileImageIV.setImageURI(it)
					}
				}

				ImagePicker.RESULT_ERROR -> {
					Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
						.show()
				}

				else -> {
					Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
				}

			}
		}

	private fun requestPermission() {
		Dexter.withContext(requireContext())  // Use `requireContext()` in Fragments
			.withPermissions(
				android.Manifest.permission.READ_EXTERNAL_STORAGE,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
				android.Manifest.permission.CAMERA
			)
			.withListener(object : MultiplePermissionsListener {
				override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
					report?.let {
						if (report.areAllPermissionsGranted()) {
							Toast.makeText(
								requireContext(),
								"All permissions granted!",
								Toast.LENGTH_SHORT
							).show()
							// Proceed with the operation

						} else if (report.isAnyPermissionPermanentlyDenied) {
							Toast.makeText(
								requireContext(),
								"Some permissions were denied permanently. Please enable them in settings.",
								Toast.LENGTH_LONG
							).show()
						}
					}
				}

				override fun onPermissionRationaleShouldBeShown(
					permissions: MutableList<PermissionRequest>?,
					token: PermissionToken?
				) {
					token?.continuePermissionRequest()
				}
			})
			.withErrorListener {
				Toast.makeText(requireContext(), "Error: ${it.name}", Toast.LENGTH_SHORT).show()
			}
			.onSameThread()
			.check()
	}


}
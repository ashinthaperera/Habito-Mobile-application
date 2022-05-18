package com.sun.todo.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val PRODUCTS = "products"

    const val YSHOPPING_PREFERENCES: String = "YShoppingPreferences"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val USER_DETAILS: String = "user_details"
    const val FNAME: String = "firstname"
    const val LNAME: String = "lastname"
    const val PHONE: String = "phone"
    const val GENDER: String = "gender"
    const val PROFILE_IMAGE: String = "profile_image"
    const val PROFILE_IMAGE_LOC: String = "image"
    const val PROFILE_COMPLETED = "profileCompleted"

    const val PRODUCT_IMAGE: String = "product_image"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_QUANTITY: String = "cart_quantity"
    const val CART_ITEMS: String = "cart_items"
    const val USER_ID: String = "user_id"
    const val PRODUCT_ID = "product_id"
    const val EXTRA_GO_TO_CART: String = "force_go_to_cart"

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val OTHER: String = "Other"

    const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: Int = 1

    const val PICK_IMAGE_REQUEST_CODE: Int = 11

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        val contentResolver = activity.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

}
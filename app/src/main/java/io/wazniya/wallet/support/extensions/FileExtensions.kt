package io.wazniya.wallet.support.extensions

import android.content.Context
import java.io.File
import java.io.IOException

/**
 * Get the application external file directory
 *
 * Application external file directory("/Android/data/<包名>/files")
 */
val Context.externalFileDirPath: String
    get() = getExternalFilesDir("")?.absolutePath ?: ""

/**
 * Get the application external cache directory
 *
 * Application cache directory ("/Android/data/<包名>/cache")
 */
val Context.externalCacheDirPath: String
    get() = externalCacheDir?.absolutePath ?: ""

/**
 * Get File object by file path
 *
 * @param filePath
 * @return nullable
 */
fun getFileByPath(filePath: String): File? = if (filePath.isBlank()) null else File(filePath)

/**
 * Determine whether the file exists
 *
 */
val File.isFileExists: Boolean get() = exists() && isFile

/**
 * Determine whether the file exists
 *
 * @param filePath
 */
fun isFileExists(filePath: String): Boolean {
    val file = getFileByPath(filePath)
    return file?.isFileExists ?: false
}

/**
 * 判断文件夹是否存在
 *
 */
val File.isDirExists: Boolean get() = exists() && isDirectory

/**
 * 判断文件夹是否存在
 *
 * @param filePath
 */
fun isDirExists(filePath: String): Boolean {
    val file = getFileByPath(filePath)
    return file?.isDirExists ?: false
}

/**
 * Determine whether the file exists, if it does not exist, determine whether it is successfully created.
 *
 * @return true The folder exists or is created successfully.  false Folder does not exist or fails to be created.
 */
fun File.createOrExistsDir(): Boolean =
// If it exists, it returns true if it is a directory, false. If it does not exist, it returns whether the creation was successful.
    if (exists()) isDirectory else mkdirs()

/**
 * Determine whether the file exists, if it does not exist, determine whether it is successfully created.
 *
 * @param filePath
 * @return true File exists or created successfully.  false The path is invalid or the file does not exist.
 */
fun createOrExistsDir(filePath: String): Boolean {
    val file = getFileByPath(filePath)
    return file?.createOrExistsDir() ?: false
}

/**
 * Determine whether the file exists, if it does not exist, determine whether it is successfully created.
 *
 * @return true File exists or created successfully.  false File does not exist or fails to be created.
 */
fun File.createOrExistsFile(): Boolean {
    if (exists()) return isFile
    if (parentFile?.createOrExistsDir() != true) return false

    return try {
        createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * Determine whether the file exists, if it does not exist, determine whether it is successfully created.
 *
 * @param filePath
 * @return true File exists or created successfully.  false The path is invalid or the file does not exist.
 */
fun createOrExistsFile(filePath: String): Boolean {
    val file = getFileByPath(filePath)
    return file?.createOrExistsFile() ?: false
}

package com.teamdefine.legalvault.main.utility

import android.R
import android.app.ProgressDialog
import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object Utility {
    suspend fun createPdfFromTextAsync(
        context: Context,
        text: String,
        fileName: String,
        view: View
    ): String? {
        return withContext(Dispatchers.IO) {
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (outputDir != null) {
                val pdfFile = File(outputDir, "$fileName.pdf")
                val pdfDocument = PdfDocument(PdfWriter(FileOutputStream(pdfFile)))

                Document(pdfDocument).use { document ->
                    document.add(Paragraph(text))
                }

                pdfFile.absolutePath
            } else {
                withContext(Dispatchers.Main) {
                    view.showSnackBar("Permission denied")
                }
                null
            }
        }
    }

    fun createPdfFromText(context: Context, text: String, fileName: String, view: View): String? {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return if (outputDir != null) {
            val pdfFile = File(outputDir, "$fileName.pdf")
            val pdfDocument = PdfDocument(PdfWriter(FileOutputStream(pdfFile)))

            Document(pdfDocument).use { document ->
                document.add(Paragraph(text))
            }

            pdfFile.absolutePath
        } else {
            view.showSnackBar("Permission denied")
            null
        }
    }


    fun getRandomText(): String {
        return "Certainly! Here's a randomly generated text of approximately 200 words:\n" +
                "\n" +
                "\"In the quiet stillness of the early morning, as the first rays of sunlight kissed the horizon, a sense of serenity enveloped the world. Birds began to chirp, breaking the silence with their cheerful melodies, while the gentle breeze whispered secrets to the swaying trees. It was a moment of pure, unadulterated beauty.\n" +
                "\n" +
                "In a bustling city, far removed from the tranquility of the countryside, people hurriedly made their way to work. Each person lost in their thoughts, chasing dreams and ambitions. The city was a symphony of honking horns, hurried footsteps, and the aroma of freshly brewed coffee wafting from street-side cafes.\n" +
                "\n" +
                "Meanwhile, a group of children played in a nearby park, their laughter filling the air with infectious joy. They raced each other, climbed on the playground equipment, and shared stories of imaginary adventures. Their innocence was a stark contrast to the complexities of adult life.\n" +
                "\n" +
                "As the day unfolded, the world continued its ceaseless motion. Some found love, while others faced heartbreak. Some achieved success, while others encountered setbacks. Life's unpredictability was its most defining characteristic.\n" +
                "\n" +
                "And so, the world turned, with each moment creating a tapestry of stories, emotions, and experiences. In the grand scheme of things, every individual was but a tiny speck in the vast cosmos, yet their lives were filled with meaning, purpose, and the possibility of endless discovery.\""
    }

    fun appendCustomDocGenerationPropmt(userPrompt: String): String {
        return "Generate a document in legal and formal wording and the document should contain all the details mentioned. $userPrompt. Add proper dates and names in the document. Also, make it to the point, but in a formal way."
    }

    fun ProgressDialog.showProgressDialog(message: String = "") {
        this.setMessage(message)
        this.setCancelable(false)
        this.show()
    }

    fun convertTimestampToDateInIST(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set the timezone to IST
        val date = Date(timestamp * 1000L)
        return dateFormat.format(date)
    }

    class NetworkException(message: String) : IOException(message)

    suspend fun extractTextFromPdfUrl(pdfUrl: String): String = withContext(Dispatchers.IO) {
        try {
            val connection = URL(pdfUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val inputStream = BufferedInputStream(connection.inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }

            val pdfData = byteArrayOutputStream.toByteArray()

            val document = PDDocument.load(pdfData)
            val pdfStripper = PDFTextStripper()

            val text = pdfStripper.getText(document)

            document.close()
            connection.disconnect()

            return@withContext text
        } catch (e: IOException) {
            throw NetworkException("Error fetching or processing the PDF: ${e.message}")
        }
    }

    fun loadImage(context: Context, imageUrl: String, view: ImageView) {
        try {
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .error(R.mipmap.sym_def_app_icon)
            Glide.with(context).load(imageUrl).apply(options).into(view)
        } catch (e: Exception) {
        }
    }
}

object CONSTANTS {
    val CLIENT_ID = "a0892a4eeac8e0113269a171861d99b3"
}
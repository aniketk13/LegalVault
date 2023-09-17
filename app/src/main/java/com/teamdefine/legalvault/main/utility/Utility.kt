package com.teamdefine.legalvault.main.utility

import android.content.Context
import android.os.Environment
import android.view.View
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import java.io.File
import java.io.FileOutputStream


object Utility {
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
}
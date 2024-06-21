package org.pets.history.service

import org.apache.pdfbox.Loader
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage

@Component
class PDFParser {
    private val pdfStripper = PDFTextStripper()

    private fun loadDocument(pdfFile: MultipartFile): PDDocument {
        return Loader.loadPDF(RandomAccessReadBuffer(pdfFile.inputStream))
    }

    fun extractText(pdfFile: MultipartFile): String {
        val document = loadDocument(pdfFile)
        return pdfStripper.getText(document)
    }

    fun extractImages(pdfFile: MultipartFile): List<BufferedImage> {
        val document = loadDocument(pdfFile)
        return document.pages.flatMap {
            extractImagesFromPDResources(it.resources)
        }
    }

    private fun extractImagesFromPDResources(pdResources: PDResources): List<BufferedImage> =
        pdResources.xObjectNames.mapNotNull {
            when (val pdXObject = pdResources.getXObject(it)) {
                is PDImageXObject -> pdXObject.image
                else -> null
            }
        }
}

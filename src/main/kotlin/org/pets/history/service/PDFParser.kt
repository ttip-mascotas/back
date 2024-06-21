package org.pets.history.service

import org.apache.pdfbox.Loader
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage

@Component
class PDFParser {
    private val pdfStripper = PDFTextStripper()
    private val supportedAnalysisContentTypes = setOf(MediaType.APPLICATION_PDF_VALUE)

    private fun load(file: MultipartFile): PDDocument {
        if (!supportedAnalysisContentTypes.contains(file.contentType!!)) {
            throw MediaTypeNotValidException(file.contentType!!, supportedAnalysisContentTypes)
        }
        return Loader.loadPDF(RandomAccessReadBuffer(file.inputStream))
    }

    fun extractText(file: MultipartFile): String {
        val document = load(file)
        return pdfStripper.getText(document)
    }

    fun extractImages(file: MultipartFile): List<BufferedImage> {
        val document = load(file)
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

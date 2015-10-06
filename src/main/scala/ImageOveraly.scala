package com.austindata

import java.io.File
import com.dmp.image._
import com.austindata.DBImageRecord._
import com.typesafe.scalalogging.LazyLogging

object ImageOverlay extends LazyLogging {

  /**
   * Return the "cropped images" from the database and process each record overlaying
   * the image onto a new one
   */
  def overlayCroppedImages {
    /* Retrieve cropped image records from database */
    DBImageRecord.croppedImages map (record => {

      val f = new File(record.filePath, record.fileName)

      /* Overlay the image */
      TIFFImage.fromFile(f).headOption map (img => {
        val newImage = OverlayImage.overlay(img, record.imageWidth.toInt, overlayLength)
        TIFFImage.toFile(f, List(newImage))
      }) match {
        case Some(true) => logger.trace("Image record with filename: {} sucessfully overlayed", record.fileName)
        case Some(false) => logger.warn("Image record with filename: {} failed to overlay", record.fileName)
        case None => logger.warn("Something went wrong processing file: {}", record.fileName)
      }

      /* Update the IS_OVERALYED database field to true */
      updateIsOverlayed(record, true) match {
        case 1 => logger.trace("Image record with filename: {} sucessfully updated", record.fileName)
        case _ => logger.warn("Image record with filename: {} failed to update IS_OVERLAYED field", record.fileName)
      }
    })
  }
}

/**
 * Overlay the cropped images
 */
object OverlyaImages extends App {

}

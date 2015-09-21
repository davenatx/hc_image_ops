package com.austindata

import java.io.File
import com.dmp.image.TIFFImage
import DBHelpers._
import com.typesafe.scalalogging.LazyLogging

object ImageReader extends LazyLogging {

  /**
   * Recursively read TIFF files from the image repository
   *
   * ToDo - Rewrite this method to use tail recursion and pattern matching
   */
  def traverse(dir: File, proc: File => Unit): Unit = {
    dir.listFiles foreach { (f) =>
      {
        if (f.isDirectory) {
          traverse(f, proc)
        } else {
          proc(f)
        }
      }
    }
  }

  /**
   * Read and insert TIFF Informatoin into database
   */
  def processTIFF(file: File): Unit = {
    logger.info("Processing File: " + file.getName + ", Dir: " + file.getParent)
    TIFFImage.fromFile(file).headOption map (img => {
      insert(
        ImageRecord(
          file.getName,
          file.getParent,
          img.compression.getOrElse(0),
          img.imageWidth.getOrElse(0),
          img.imageLength.getOrElse(0),
          img.xResolution.getOrElse(0),
          img.yResolution.getOrElse(0)
        )
      )
    })
  }

  val readAndInsertFunc = traverse(_: File, processTIFF)
}

object PopulateDatabase extends App {
  dropTables
  createTables
  val rootDir = new File("//ADINAS01/HC/R/1965/01")
  ImageReader.readAndInsertFunc(rootDir)
}


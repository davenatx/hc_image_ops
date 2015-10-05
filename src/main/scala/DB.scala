package com.austindata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{ GetResult, StaticQuery => Q }

import java.sql.Date

object DBImageRecord {

  /* Domain Object representing an Image Record */
  case class ImageRecord(fileName: String, filePath: String, fileDate: Date, compression: Int, imageWidth: Long, imageLength: Long, xResolution: Long, yResolution: Long, isOverlayed: Boolean, id: Option[Int] = None)

  /* Slick Table object.  The * projection has bi-directional mapping (<>) to ImageRecord */
  class ImageRecords(tag: Tag) extends Table[ImageRecord](tag, "IMAGE_RECORDS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def fileName = column[String]("FNAME")
    def filePath = column[String]("FPATH")
    def fileDate = column[Date]("FILEDATE")
    def compression = column[Int]("COMPRESSION")
    def imageWidth = column[Long]("IMG_WIDTH")
    def imageLength = column[Long]("IMG_LENGTH")
    def xResolution = column[Long]("X_RESOLUTION")
    def yResolution = column[Long]("Y_RESOLUTION")
    def isOverlayed = column[Boolean]("IS_OVERLAYED", O.Default(false))
    def * = (fileName, filePath, fileDate, compression, imageWidth, imageLength, xResolution, yResolution, isOverlayed, id.?) <> (ImageRecord.tupled, ImageRecord.unapply)
    def fileNameIndex = index("IDX_FNAME", fileName, unique = true)
    def fileDateIndex = index("IDX_FDATE", fileDate, unique = false)
    def imageWidthIndex = index("IDX_IMG_WIDTH", imageWidth, unique = false)
    def imageLengthIndex = index("IDX_IMG_LENGTH", imageLength, unique = false)
    def isOverlayedIndex = index("IDX_IS_OVERLAY", isOverlayed, unique = false)
  }

  /* The query interface for the IndexRecords table */
  val imageRecords: TableQuery[ImageRecords] = TableQuery[ImageRecords]

  /* Create the tables */
  def createTables {
    database withSession { implicit session =>
      imageRecords.ddl.create
    }
  }

  /* Drop the tables */
  def dropTables {
    database withSession { implicit session =>
      imageRecords.ddl.drop
    }
  }

  /* Insert one IndexRecord into IndexRecords */
  def insert(record: ImageRecord) {
    database withSession { implicit session =>
      imageRecords += (record)
    }
  }
  /* Batch Insert a sequence of IndexRecord into IndexRecords */
  def insert(records: Seq[ImageRecord]) {
    database withSession { implicit session =>
      imageRecords ++= records
    }
  }

  // Implicit conversion to map ResultSet to ImageRecord
  implicit val getImageRecordResult = GetResult(r => ImageRecord(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  /* Query to retrieve cropped images where the DPI is 300 or 72 */
  def croppedImages: List[ImageRecord] = {
    database withSession { implicit session =>
      Q.queryNA[ImageRecord]("""
      SELECT *
      FROM IMAGE_RECORDS
      WHERE IMG_LENGTH < IMG_WIDTH
      AND (X_RESOLUTION = 300 OR X_RESOLUTION=72)
      ORDER BY FILEDATE
      """).list

    }
  }
}
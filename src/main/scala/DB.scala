package com.austindata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{ StaticQuery => Q }

import java.sql.Date

/* Domain Object representing an Image Record */
case class ImageRecord(fileName: String, filePath: String, compression: Int, imageWidth: Long, imageLength: Long, xResolution: Long, yResolution: Long, id: Option[Int] = None)

/* Slick Table object.  The * projection has bi-directional mapping (<>) to ImageRecord */
class ImageRecords(tag: Tag)
    extends Table[ImageRecord](tag, "IMAGE_RECORDS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def fileName = column[String]("FNAME")
  def filePath = column[String]("FPATH")
  def compression = column[Int]("COMPRESSION")
  def imageWidth = column[Long]("IMG_WIDTH")
  def imageLength = column[Long]("IMG_LENGTH")
  def xResolution = column[Long]("X_RESOLUTION")
  def yResolution = column[Long]("Y_RESOLUTION")
  def * = (fileName, filePath, compression, imageWidth, imageLength, xResolution, yResolution, id.?) <> (ImageRecord.tupled, ImageRecord.unapply)
  def fileNameIndex = index("IDX_FNAME", fileName, unique = false)
  def imageWidthIndex = index("IDX_IMG_WIDTH", imageWidth, unique = false)
  def imageLengthIndex = index("IDX_IMG_LENGTH", imageLength, unique = false)
}

/* Helper ojbect to perform Database operations */
object DBHelpers {

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

  /*
  
  /* Domain object representing the fields returned by OPRRecordsByYear */
  case class QueryRecord(recordType: String, documentType: String, volume: String, page: String, fileDate: Date, fileName: String)


  /* Query OPR Records by Year.  This is a distinct query because records can be represented multiple times due to the number of parties, etc... */
  def OPRRecordsByYear(year: String): List[QueryRecord] = {
    database withSession { implicit session =>

      val query = Q.query[String, (String, String, String, String, Date, String)]("""
      SELECT DISTINCT RECTYP, DOCTYP, VOLUME, CAST(PAGE as INT), FILEDATE, FNAME 
      FROM INDEX_RECORDS 
      WHERE EXTRACT(YEAR FROM FILEDATE) = ? AND RECTYP = 'OPR' ORDER BY VOLUME, CAST(PAGE as INT), FNAME 
    """)

      query(year).list map (r => QueryRecord(r._1, r._2, r._3, r._4, r._5, r._6))
    }
  }

  /* Query Records by Year.  This is a distinct query because records can be represented multiple times due to the number of parties, etc... */
  def recordsByYear(year: String): List[QueryRecord] = {
    database withSession { implicit session =>

      val query = Q.query[String, (String, String, String, String, Date, String)]("""
      SELECT DISTINCT RECTYP, DOCTYP, VOLUME, PAGE, FILEDATE, FNAME 
      FROM INDEX_RECORDS 
      WHERE EXTRACT(YEAR FROM FILEDATE) = ? ORDER BY VOLUME, PAGE, FILEDATE 
    """)

      query(year).list map (r => QueryRecord(r._1, r._2, r._3, r._4, r._5, r._6))
    }
  }
  */
}
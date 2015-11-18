# hc_image_ops
The purpose of this project is to identify cropped images (images where the image length is less than the image width) and overlay the image onto a standard size page.  This is accomplished by reading the "HC" image repository and inserting a database record representing each TIFF image into the included H2 database.  This step is necessary because we need to use SQL to investigate this data to determine how to complete this project.  For example, we need to not only determine which images are "cropped", but we also need to identify the different resolutions in order to create a process to "fix" them.

All of the images presently in the "HC" image repository are read and stored in the accompanying H2 database.

After this project was designed, it was discovered it would have multiple segments:

  * The segment between 1/1/1920 - 9/28/1984 is known as [Phase1](phase1.md).

  * The segment between 8/25/1848 - 12/31/1919 is known as [Phase2](phase2.md).

##Database 

This database has one table, IMAGE_RECORDS.  This table has the following fields:

  * FNAME - File Name
  * FPATH - File Path
  * FILEDATE - File Date created by the image repository path. e.g. .../YYYY/MM/DD
  * COMPRESSION - TIFF compression
  * IMG_WIDTH - Pixel Width of Image
  * IMG_LENGTH - Pixel Length of Image
  * X_RESOLUTION - X Resolution in DPI
  * Y_RESOLUTION - Y Resolution in DPI
  * IS_OVERLAYED - Indicates if the image has been overlayed
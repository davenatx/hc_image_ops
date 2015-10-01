# hc_image_ops
The purpose of this project is to identify cropped images (images where the image length is less than the image width) and overlay the image onto a standard size page.  This is accomplished by reading the "HC" image repository and inserting a database record representing each TIFF image into the included H2 database.  This step is necessary because we need to use SQL to investigate this data to determine how to complete this project.  For example, we need to not only determine which images are "cropped", but we also need to identify the different resolutions in order to create a process to "fix" them.

All of the images presently in the "HC" image repository are read and stored in the accompanying H2 database.  

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

##SQL

Below is some helpful SQL for investigating this database:

This SQL creates an index on the IMG_LENGTH field and orders it in descending order.  Presently, there does not appear to be a way for Slick 2 to do this:  https://github.com/slick/slick/issues/1035.  This is necessary because it helps H2 perform order by operations:

````
CREATE INDEX IDX_IMG_LENGTH_DESC ON IMAGE_RECORDS(IMG_LENGTH DESC);  
````

Query all the records and order by IMG_LENGTH in descending order:

````
SELECT * 
FROM IMAGE_RECORDS
ORDER BY IMG_LENGTH DESC;
````

Query distinct image lengths where resolution is 300:

````
SELECT DISTINCT IMG_LENGTH
FROM IMAGE_RECORDS 
WHERE X_RESOLUTION = 300
ORDER BY IMG_LENGTH DESC;
````

Find the average image length where the length is between 5000 and 5999 for 300 DPI::

````
SELECT DISTINCT AVG(IMG_LENGTH)
FROM IMAGE_RECORDS 
WHERE X_RESOLUTION = 300
AND IMG_LENGTH BETWEEN 5000 AND 5999;

AVG(IMG_LENGTH)  
5527
````

Find the number of records in the database:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS;

COUNT(*)  
354623
````

Find the number of unique file names in the database:

````
SELECT DISTINCT COUNT(FNAME)
FROM IMAGE_RECORDS;

COUNT(FNAME)  
354623
````

Find the number of records where the X_RESOLUTION does not equal the Y_RESOLUTION:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION != Y_RESOLUTION;

COUNT(*)  
0
````

Find the distinct X_RESOLUTIONs:

````
SELECT DISTINCT X_RESOLUTION
FROM IMAGE_RECORDS;

X_RESOLUTION  
240
72
96
300
400
200
````


Find the number of images with an X_RESOLTUION of 400:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 400;

COUNT(*)  
276
````

Find the number of images with an X_RESOLTUION of 300:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 300;

COUNT(*)  
70008
````

Find the number of images with an X_RESOLTUION of 240:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 240;

COUNT(*)  
25701
````

Find the number of images with an X_RESOLTUION of 200:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 200;

COUNT(*)  
258341
````

Find the number of images with an X_RESOLTUION of 96:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 96;

COUNT(*)  
1
````

Find the number of images with an X_RESOLTUION of 72:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE X_RESOLUTION = 72;

COUNT(*)  
269
````

Find the number of images where the image length is less than the image width:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH;

COUNT(*)  
19265
````

Find the X_RESOLUTIONs that exist in the set of images where the image length is less than the image width:

````
SELECT DISTINCT(X_RESOLUTION)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH;

X_RESOLUTION  
72
200
300
400
````

Find the number of images where the X_RESOLUTION is 72 and the image length is less than the image width:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH
AND X_RESOLUTION = 72;

COUNT(*)  
184
````

Find the number of images where the X_RESOLUTION is 200 and the image length is less than the image width:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH
AND X_RESOLUTION = 200;

COUNT(*)  
2
````

Find the number of images where the X_RESOLUTION is 300 and the image length is less than the image width:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH
AND X_RESOLUTION = 300;

COUNT(*)  
19076
````

Find the number of images where the X_RESOLUTION is 400 and the image length is less than the image width:

````
SELECT COUNT(*)
FROM IMAGE_RECORDS
WHERE IMG_LENGTH < IMG_WIDTH
AND X_RESOLUTION = 400;

COUNT(*)  
3
````

Find **bad** images where the IMG_LENGTH is 1:

````
SELECT *
FROM IMAGE_RECORDS
WHERE IMG_LENGTH = 1;

FNAME  	    FPATH  	           FILEDATE  	COMPRESSION  	IMG_WIDTH  	IMG_LENGTH  	X_RESOLUTION  	Y_RESOLUTION  	ID  
127284.007	\HC\R\1979\03\27   1979-03-27	4	            1696	    1	            200	            200	            221926
171445.001	\HC\R\1983\06\28   1983-06-28	4	            1696	    1	            200	            200	            287401
````

##Identified Cropped Image Segments

* For the 184 images in this segment with 72 DPI, determine the proper image length for the image to overlay on and process this segment.
  
  It appears these images are really 300 DPI images that were cropped and had the resolution incorrectly set:
  
  ````
  FNAME  	    FPATH  	           FILEDATE  	COMPRESSION  	IMG_WIDTH  	IMG_LENGTH  	X_RESOLUTION  	Y_RESOLUTION  	ID  
  53255.001	    \HC\R\1965\09\23   1965-09-23	4	            3568	    5536	        300	            300	            119627
  53255.002	    \HC\R\1965\09\23   1965-09-23	4	            3568	    5536	        300	            300	            119628
  53255.003     \HC\R\1965\09\23   1965-09-23	4	            3568	    1859	        72	            72	            119629
  ````

* For the 2 images in this segment with 200 DPI, handle these **manually**:
  
  \HC\R\1979\03\27\127284.007 **(Bad Image)**
  \HC\R\1983\06\28\171445.001 **(Bad Image)**

* For the 19,076 images in this segment with 300 DPI, use an image length of 5527 (calculated above) as the image length for the image to overlay on.

* For the 3 images in this segment with 400 DPI, handle these **manually**:
  
  \HC\R\1943\10\04\1943128046.001 **(Plat)**
  \HC\R\1964\03\23\49080.001 **(Plat)**
  \HC\R\1973\11\06\85188.016 **(CCR Map)**

##Solution Overview

Outline of high-level approach:

1. Determine if each FNAME (filename) in the database is unique. **Yes**

2. Determine an average image length to use as a default iamge length for the image to overlay on.  Becuase the 72 DPI images are really 300 DPI images, use one factor.  The average image length calculated above is 5527.  However, it appears 5536 is the most common image length in this semgent.  **which one should I use?**

3. Query the cropped images that are 72 DPI or 300 DPI together becuase they are really the same resolution:

  ````
  SELECT COUNT(*)
  FROM IMAGE_RECORDS
  WHERE IMG_LENGTH < IMG_WIDTH
  AND (X_RESOLUTION = 300 OR X_RESOLUTION=72);
  
  COUNT(*)  
  19260
  ````

4. Process the results, and overwrite the existing local image with the OverlayImage.overlay().

5. For each image that is overlayed, update the OVERLAY field in the database to true. **I need to alter table to add this column as a boolean with a default value of false**

6. After these images have been "fixed" I need to devise a way to export the images where OVERLAY = true.  I not only neeed to export the overlayed images, but I need to include all of their pages.  I would like to do this by year.  Therefore, the images should be exported to a yearly folder.  From here, we can QC them, and if we are happy, we can index them to replace the cropped images on the image repository.
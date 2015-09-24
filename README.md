# hc_image_ops
The purpose of this project is to identify cropped images (image width is greater than image length) and overlay the image onto a standard size page.  This is accomplished by reading the "HC" image repository and inserting a database record representing each TIFF file.  This step is necessary because we need to use SQL to investigate this data to determine how to complete this project.  For example, we need to determine which images are "cropped".  This is most likely going to be images where the length is less than the width.  Next we need to determine what the default length of the template image should be we overlay the cropped image onto.  

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

This SQL creates an index on the IMG_LENGTH field and orders it in descending order.  It is necessary because it helps H2 perform order by operations:

````
CREATE INDEX IDX_IMG_LENTH_DESC ON IMAGE_RECORDS(IMG_LENGTH DESC);  
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

##Outcomes for images where image length is less than image width:

* For the 184 images in this segment with 72 DPI, determine the proper image length for the image to overlay on and process this segment.
* For the 2 images in this segment with 200 DPI, handle these manually
* For the 19,076 images in this segment with 300 DPI, use an image length of 5527 (calculated above) as the image length for the image to overlay on.
* For the 3 images in this segment with 400 DPI, handle these manually  

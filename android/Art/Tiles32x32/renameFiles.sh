#!/bin/sh

for i in Tile* 
do
	index=$(echo $i | cut -d '-' -f2 | cut -d '.' -f1)
	echo "mv $i Tile$index.png"
	mv $i Tile$index.png
done


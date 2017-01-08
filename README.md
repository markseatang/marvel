# Marvel Comics Proxy
By Mark Seatang, using the Marvel Comics API
Data provided by Marvel.  2017 MARVEL

To compile:
make

To run:
sh run.sh

To use:
In your browser go to localhost:8000/marvelous
To specify args:

localhost:8000/marvelous?id=2

will request comicID no. 2

localhost:8000/marvelous?id=2,3,4,5,6

will request comicID no. 2, 3, 4, 5 and 6

Currently this program is single threaded and has caching.
The output is just whatever .json is produced from the marvel api call.

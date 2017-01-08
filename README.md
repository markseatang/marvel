# Marvel Comics Proxy
By Mark Seatang, using the Marvel Comics API
Data provided by Marvel.  2017 MARVEL

This proxy allows multiple requests to the marvel API for details about comics by their comic ID at once.
Usually the Marvel API doesn't allow for multiple accesses at once, so this proxy helps with that.
Currently this program is single threaded and has caching.
The output is just whatever .json is produced from the marvel api call.

### Compiling:
Use the makefile
```
make
```

### Running the server
Start the server from the console using
```
sh run.sh
```

The console will prompt you to type any key and press enter to gracefully shutdown the server.
It might print some interesting diagnostic stuff to the terminal.

### Usage
Access the proxy at:
```
localhost:8000/marvelous
```
Of course, this won't actually show you anything until you give some arguments.

To specify arguments, append them to the path in the following comma separated format:

```
localhost:8000/marvelous?id=<arg1>,<arg2>,<arg3>...,<arg_n>
```

So the following request:
```
localhost:8000/marvelous?id=2
```
will request comicID no. 2

This subsequent request:
```
localhost:8000/marvelous?id=2,3,4,5,6
```
will request comicID no. 2, 3, 4, 5 and 6



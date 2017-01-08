# Marvel Comics Proxy
By Mark Seatang, using the Marvel Comics API
Data provided by Marvel.  2017 MARVEL

This proxy allows multiple requests to the marvel API for details about comics by their comic ID at once.
Usually the Marvel API doesn't allow for multiple accesses at once, so this proxy helps with that.
This program is multithreaded and has caching. It combines the outputs from the Marvel API into a 
single json output, where each of the results is an element in an array. In other words, this 
successfully handles a list of comic ID numbers and outputs the results as an array.

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



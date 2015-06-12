Jinpeng Bi(Koby)
jbi3@usfca.edu
20262008

I use a recursion to get all path of the files. Then I create a nested 
TreeMap<String, TreeMap<String, TreeSet<Integer>>> to store the word first.
And then according to the word to store a TreeMap of the path and the index
of the path in it. And to add into the TreeSet if it has more than one index.
Then I write all things in TreeMap into the output file.

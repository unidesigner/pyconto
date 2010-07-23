def remove_duplicates(list):
    unique_items = [list[1]]
    for item in list:
        is_unique = True
        for unique_item in unique_items:
            if item == unique_item:
                is_unique = False
                break
        
        if is_unique:
            unique_items.append(item)
            
    return unique_items
        

if __name__ == '__main__':
    from optparse import OptionParser
    import sys
    
    usage="python remove_duplicates.py [OPTION]... input_file"
    parser = OptionParser(usage)
    parser.add_option("-f", "--file", dest="filename", help="output unique lines to FILENAME (default=stdout)")

    (options,args) = parser.parse_args()

    if len(args) < 1:
        print "No input file!"
        sys.exit(0)
    
    input_filename = args[0]
                
    file = open(input_filename,"r")
    lines = file.readlines()
    file.close()

    unique_lines = remove_duplicates(lines)
        
    if options.filename == None:
        for line in unique_lines:
            print line,
    else:
        output_file = open(options.filename,"w")
        for line in unique_lines:
            output_file.write(line)
        output_file.close()     
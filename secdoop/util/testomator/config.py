from xml.dom import minidom

class ConfigFile(object): 
    '''The ConfigFile object is used to access and modify the 
    hadoop configuration files.

    '''

    def __init__(self, filename="default.xml"):
        '''Initializes the config object and processes the data from the 
        filename parameter.  The config files must be in xml format.

        '''

        self._properties = {} 
        self._elements = {} 
        self._file = open(filename, 'r+')
        input_xml = self._file.read()
        normalized = ''.join([line.strip() for line in input_xml.splitlines()])
        self.xml = minidom.parseString(normalized)
        self. _parse_xml()
        self.filename = filename

    def _parse_xml(self):
        raw_properties = self.xml.getElementsByTagName("property")

        #for each property get its children
        name = None
        value = None
        for p in raw_properties:
            for c in p.childNodes:
                if c.nodeName == "name":
                    prop_name = str(c.childNodes[0].data)
                elif c.nodeName == "value":
                    prop_val = str(c.childNodes[0].data)
                    self._elements[prop_name] = c.childNodes[0]

            self._properties[prop_name] = prop_val

    def get(self, key):
        '''Returns the value associated with the key in the
        properties dictionary.

        '''

        return self._properties[key]

    def set(self, key, value):
        '''Sets the name item in the dicionary to the value parameter.

        '''

        self._properties[key] = value
        self._elements[key].data = u'%s' % value
        # TODO: fold write() into set()

    def keys(self):
        '''Returns the keys associated with the properties array.
        
        '''

        return self._properties.keys()


class Config(object):
    '''
    The config object used to manage configuration of the hadoop cluster.
    '''

    def __init__(self, *args):
        '''
        The *args parameter is a tuple of ConfigFiles.
        '''
        self._map = {} # map is the dictionary that holds the properties and associated files

        # iterate through the tuple of ConfigFiles
        for f in args:    
            # get the keys associated with the ConfigFile
            keys = f.keys()
            # iterate through the keys
            for k in keys:
                # map the key to the ConfigFile
                self._map[k] = f

    def get(self, key):
        '''
        Returns the value associated with the key parameter.
        '''
        #get the ConfigFile
        c_f = self._map[key]
        return c_f.get(key)


    def set(self, key, value):
        '''
        Sets the value associated with the key to the value parameter.
        '''
        #get the config file
        c_f = self._map[key]
        c_f.set(key, value)

    def write(self):
        '''
        Updates to disk all of the properties and ConfigFiles that are
        associated with this Config object.
        '''

    def keys(self):
        '''
        Returns all of the keys that are controlled through this config
        object.
        '''
        return self._map.keys()

if __name__ == '__main__':

    fig = ConfigFile(filename='tests/test.xml')
    print 'maximum-initialized-jobs-per-user: ' + str(fig.get('mapred.capacity-scheduler.default-maximum-initialized-jobs-per-user'))
    fig.set('mapred.capacity-scheduler.default-maximum-initialized-jobs-per-user', 5)
    print 'modified maximum-initialized-jobs-per-user: ' +  str(fig.get('mapred.capacity-scheduler.default-maximum-initialized-jobs-per-user'))
    cf1 = ConfigFile('tests/test.xml')
    cf2 = ConfigFile('tests/test2.xml')

    configuration = Config(cf1, cf2)
    keys = configuration.keys()
    for k in keys:
        print k + ' ' + configuration.get(k)

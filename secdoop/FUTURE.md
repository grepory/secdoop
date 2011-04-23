Future Work
===========

### Support for HDFS Archive files
- If encrypting many smaller files (i.e. smaller than an HDFS block), place
  all of the files into an HDFS archive, and then encrypt that.

### Secure Cryptographic Key Management
- We use the PBE keys in JCE with keys stored statically in source. Add key
  management that allows the EncryptMapper/DecryptMapper to get the keys from a
  keystore (statically).

### Multi-line record format
- Create a new InputFormat and RecordReader that passes multiple lines to each
  map() call.

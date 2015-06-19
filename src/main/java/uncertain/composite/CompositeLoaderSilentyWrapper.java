/*
 * Created on 2011-9-29 下午08:08:15
 * $Id$
 */
package uncertain.composite;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import uncertain.exception.BuiltinExceptionFactory;

/**
 * Wrap an actual CompositeLoader, catch any possible exception during parsing,
 * throw as runtime exception that does not need explicit catch
 */
public class CompositeLoaderSilentyWrapper {

    CompositeLoader loader;

    public CompositeLoaderSilentyWrapper(CompositeLoader loader) {
        this.loader = loader;
    }

    public CompositeMap loadFromString(String str) {
        try {
            return loader.loadFromString(str);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null, "("
                    + str + ")", ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException("(" + str
                    + ")", sex);
        }
    }

    public CompositeMap loadFromString(String str, String charsetName) {
        try {
            return loader.loadFromString(str, charsetName);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null, "("
                    + str + ")", ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException("(" + str
                    + ")", sex);
        }
    }

    public CompositeMap loadFromStream(InputStream stream) {
        try {
            return loader.loadFromStream(stream);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    "instance of " + stream.getClass(), ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(
                    "instance of " + stream.getClass(), sex);
        }
    }

    public CompositeMap loadByURL(String url) {
        try {
            return loader.loadByURL(url);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    url, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(url, sex);
        }
    }

    public CompositeMap loadByFullFilePath_NC(String file_name) {
        try {
            return loader.loadByFullFilePath_NC(file_name);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    file_name, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(file_name,
                    sex);
        }
    }

    public CompositeMap loadByFullFilePath(String file_name) {
        try {
            return loader.loadByFullFilePath(file_name);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    file_name, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(file_name,
                    sex);
        }
    }

    public CompositeMap loadByFile(String file_name) {
        try {
            return loader.loadByFile(file_name);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    file_name, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(file_name,
                    sex);
        }
    }

    public CompositeMap load(String resource_name) {
        try {
            return loader.load(resource_name);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    resource_name, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(
                    resource_name, sex);
        }
    }

    public CompositeMap loadFromClassPath(String full_name) {
        try {
            return loader.loadFromClassPath(full_name);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    full_name, ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(full_name,
                    sex);
        }
    }

    public CompositeMap loadFromClassPath(String full_name, String file_ext) {
        try {
            return loader.loadFromClassPath(full_name, file_ext);
        } catch (IOException ex) {
            throw BuiltinExceptionFactory.createResourceLoadException(null,
                    full_name + "[." + file_ext + "]", ex);
        } catch (SAXException sex) {
            throw BuiltinExceptionFactory.createXmlGrammarException(full_name
                    + "[." + file_ext + "]", sex);
        }
    }

}

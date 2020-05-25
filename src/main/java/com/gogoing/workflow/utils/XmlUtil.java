package com.gogoing.workflow.utils;

import javax.xml.stream.XMLInputFactory;

/**
 * @apiNote 此类直接从activiti-app下复制过来
 * @author lhj
 */
public class XmlUtil {

	/**
	 * 'safe' is here reflecting:
	 * http://activiti.org/userguide/index.html#advanced.safe.bpmn.xml
	 */
	public static XMLInputFactory createSafeXmlInputFactory() {
		XMLInputFactory xif = XMLInputFactory.newInstance();
		if (xif.isPropertySupported(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES)) {
			xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
			        false);
		}

		if (xif.isPropertySupported(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES)) {
			xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
			        false);
		}

		if (xif.isPropertySupported(XMLInputFactory.SUPPORT_DTD)) {
			xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		}
		return xif;
	}

}

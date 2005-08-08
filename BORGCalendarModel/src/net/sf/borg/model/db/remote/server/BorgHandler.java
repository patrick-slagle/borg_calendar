/*
This file is part of BORG.
 
	BORG is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
 
	BORG is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
 
	You should have received a copy of the GNU General Public License
	along with BORG; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
Copyright 2004 by Mohan Embar - http://www.thisiscool.com/
 */

package net.sf.borg.model.db.remote.server;

import java.util.HashMap;
import java.util.Map;

import net.sf.borg.common.util.Errmsg;
import net.sf.borg.common.util.XTree;
import net.sf.borg.model.AppointmentKeyFilter;
import net.sf.borg.model.BorgOption;
import net.sf.borg.model.db.BeanDB;
import net.sf.borg.model.db.BeanDataFactoryFactory;
import net.sf.borg.model.db.DBException;
import net.sf.borg.model.db.IBeanDataFactory;
import net.sf.borg.model.db.KeyedBean;
import net.sf.borg.model.db.remote.IRemoteProxy;
import net.sf.borg.model.db.remote.XmlObjectHelper;

/**
 * The server-side component of the BORG remote invocation framework.
 * This is designed for single-threaded invocation only.
 */
public class BorgHandler
{
	public BorgHandler(String url)
	{
		this.url = url;
	}

	// IRemoteProxy overrides
	public final String execute(String strXml)
	{
		Object result = null;
		
//		System.out.println("[INPUT] "+strXml);
		
		try
		{
			XTree xmlParms = XTree.readFromBuffer(strXml);
			IRemoteProxy.Parms parms =
				(IRemoteProxy.Parms) XmlObjectHelper.fromXml(xmlParms);
			
			// Figure out what we need to do
			BeanDB beanDB = getBeanDB(parms.getMyClass(), parms.getUser());
			String cmd = parms.getCommand();
			
			if (cmd.equals("readAll"))
			{
				result = beanDB.readAll();
			}
			else if (cmd.equals("readObj"))
			{
				int key = ((Integer) parms.getArgs()).intValue();
				result = beanDB.readObj(key);
			}
			else if (cmd.equals("delete"))
			{
				int key = ((Integer) parms.getArgs()).intValue();
				beanDB.delete(key);
			}
			else if (cmd.equals("getOption"))
			{
				String key = (String) parms.getArgs();
				result = beanDB.getOption(key);
			}
			else if (cmd.equals("getOptions"))
			{
				result = beanDB.getOptions();
			}
			else if (cmd.equals("getTodoKeys"))
			{
				result = ((AppointmentKeyFilter) beanDB).getTodoKeys();
			}
			else if (cmd.equals("getRepeatKeys"))
			{
				result = ((AppointmentKeyFilter) beanDB).getRepeatKeys();
			}
			else if (cmd.equals("nextkey"))
			{
				result = new Integer(beanDB.nextkey());
			}
			else if (cmd.equals("setOption"))
			{
				BorgOption option = (BorgOption) parms.getArgs();
				beanDB.setOption(option);
			}
			else if (cmd.equals("addObj") || cmd.equals("updateObj"))
			{
				IRemoteProxy.ComposedObject agg =
					(IRemoteProxy.ComposedObject) parms.getArgs();
				KeyedBean bean = (KeyedBean) agg.getO1();
				boolean crypt = ((Boolean) agg.getO2()).booleanValue();
				
				if (cmd.equals("addObj"))
					beanDB.addObj(bean,crypt);
				else
					beanDB.updateObj(bean,crypt);
			}
			else
				throw new UnsupportedOperationException(cmd);
		}
		catch (Exception e)
		{
			if (e instanceof DBException)
				result = e;
			else
				result = new DBException(e.getClass().getName() + ": "
						+ e.getLocalizedMessage());
		}
		
		String resultString = XmlObjectHelper.toXml(result).toString();
//		System.out.println("[OUTPUT] "+resultString);
		return resultString;
	}
	
	// private //
	private Map beanDBMap = new HashMap();
	private String url;
	
	private BeanDB getBeanDB(Class cls, String username)
		throws Exception
	{
		String key = cls.getName() + "_" + username;
			// TODO: bounce logged off users from the map
		
		BeanDB beanDB = (BeanDB) beanDBMap.get(key);
		if (beanDB == null)
		{
			String db = url;
			StringBuffer tmp = new StringBuffer(db);
			IBeanDataFactory factory =
				BeanDataFactoryFactory
					.getInstance()
					.getFactory(tmp, false, true);
			db = tmp.toString();
				// allow the factory to tweak the URL
			
			boolean console = Errmsg.console();
			try
			{
				Errmsg.console(true);
				beanDB = factory.create(cls, db, username);
				beanDBMap.put(key,beanDB);
			}
			finally
			{
				Errmsg.console(console);
			}
		}
		return beanDB;
	}
}

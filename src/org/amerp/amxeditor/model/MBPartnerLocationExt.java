/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/**
 * 
 */
/**
 * @author luisamesty
 *
 */

package org.amerp.amxeditor.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.*;



/**
 * Partner Location Extended Model
 * 
 * @author Luis Amesty
 *
 */
public class MBPartnerLocationExt extends X_C_BPartner_Location {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8412652367051443276L;

	/**
	 * Get Locations for BPartner
	 * 
	 * @param ctx
	 *            context
	 * @param C_BPartner_ID
	 *            bp
	 * @return array of locations
	 * @deprecated Since 3.5.3a. Please use
	 *             {@link #getForBPartner(Properties, int, String)}.
	 */
	public static MBPartnerLocationExt[] getForBPartner(Properties ctx,
			int C_BPartner_ID) {
		return getForBPartner(ctx, C_BPartner_ID, null);
	}

	/**
	 * Get Locations for BPartner
	 * 
	 * @param ctx
	 *            context
	 * @param C_BPartner_ID
	 *            bp
	 * @param trxName
	 * @return array of locations
	 */
	public static MBPartnerLocationExt[] getForBPartner(Properties ctx,
			int C_BPartner_ID, String trxName) {
		List<MBPartnerLocationExt> list = new Query(ctx, Table_Name,
				"C_BPartner_ID=?", trxName).setParameters(C_BPartner_ID).list();
		MBPartnerLocationExt[] retValue = new MBPartnerLocationExt[list.size()];
		list.toArray(retValue);
		return retValue;
	} // getForBPartner

	/**************************************************************************
	 * Default Constructor
	 * 
	 * @param ctx
	 *            context
	 * @param C_BPartner_Location_ID
	 *            id
	 * @param trxName
	 *            transaction
	 */
	public MBPartnerLocationExt(Properties ctx, int C_BPartner_Location_ID,
			String trxName) {
		super(ctx, C_BPartner_Location_ID, trxName);
		if (C_BPartner_Location_ID == 0) {
			setName(".");
			//
			setIsShipTo(true);
			setIsRemitTo(true);
			setIsPayFrom(true);
			setIsBillTo(true);
		}
	} // MBPartner_Location

	/**
	 * BP Parent Constructor
	 * 
	 * @param bp
	 *            partner
	 */
	public MBPartnerLocationExt(MBPartner bp) {
		this(bp.getCtx(), 0, bp.get_TrxName());
		setClientOrg(bp);
		// may (still) be 0
		// set_ValueNoCheck("C_BPartner_ID", new Integer(bp.getC_BPartner_ID()));
		int C_BPartner_ID = Integer.valueOf(bp.getC_BPartner_ID());
		set_ValueNoCheck("C_BPartner_ID", C_BPartner_ID);
	} // MBPartner_Location

	/**
	 * Constructor from ResultSet row
	 * 
	 * @param ctx
	 *            context
	 * @param rs
	 *            current row of result set to be loaded
	 * @param trxName
	 *            transaction
	 */
	public MBPartnerLocationExt(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	} // MBPartner_Location

	/** Cached Location */
	private MLocationExt m_location = null;
	/** Unique Name */
	private String m_uniqueName = null;
	private int m_unique = 0;

	/**
	 * Get Location/Address
	 * 
	 * @param requery get again the location from DB - please note that if used out of transaction the result is get from the cache
	 * @return location
	 */
	public MLocationExt getLocation(boolean requery) {
		if (requery || m_location == null) {
//			m_location = (MLocationExt) MLocationExt.get(getCtx(), getC_Location_ID(), get_TrxName());
			int C_Location_ID = 0;
			C_Location_ID = (int) getC_Location_ID();
			m_location = (MLocationExt) MLocationExt.get(getCtx(), C_Location_ID, get_TrxName());
		}
		return m_location;
	} // getLocation

	/**
	 * String Representation
	 * 
	 * @return info
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("MBPartner_Location[ID=")
				.append(get_ID()).append(",C_Location_ID=")
				.append(getC_Location_ID()).append(",Name=").append(getName())
				.append("]");
		return sb.toString();
	} // toString

	/**************************************************************************
	 * Before Save. - Set Name
	 * 
	 * @param newRecord
	 *            new
	 * @return save
	 */
	protected boolean beforeSave(boolean newRecord) {
		
//		if (this.get_ID()== 0)
//			return false;
//		if (getC_Location_ID() == 0)
//			return false;

		// Set New Name
		if (".".equals(getName())) {
			MLocationExt address = getLocation(true);
			setName(getBPLocName(address));
		}
		return true;
	} // beforeSave

	/**
	 * Make name Unique
	 * 
	 * @param address
	 *            address
	 */
	private void makeUnique(MLocationExt address) {
		m_uniqueName = "";

		// 0 - City
		if (m_unique >= 0 || m_uniqueName.length() == 0) {
			String xx = address.getCity();
			if (xx != null && xx.length() > 0)
				m_uniqueName = xx;
		}
		// 1 + Address1
		if (m_unique >= 1 || m_uniqueName.length() == 0) {
			String xx = address.getAddress1();
			if (xx != null && xx.length() > 0) {
				if (m_uniqueName.length() > 0)
					m_uniqueName += " ";
				m_uniqueName += xx;
			}
		}
		// 2 + Address2
		if (m_unique >= 2 || m_uniqueName.length() == 0) {
			String xx = address.getAddress2();
			if (xx != null && xx.length() > 0) {
				if (m_uniqueName.length() > 0)
					m_uniqueName += " ";
				m_uniqueName += xx;
			}
		}
		// 3 - Region
		if (m_unique >= 3 || m_uniqueName.length() == 0) {
			String xx = address.getRegionName(true);
			if (xx != null && xx.length() > 0) {
				if (m_uniqueName.length() > 0)
						m_uniqueName += " ";
				m_uniqueName += xx;
			}
		}
		// 4 - ID
		if (m_unique >= 4 || m_uniqueName.length() == 0) {
			int id = get_ID();
			if (id == 0)
				id = address.get_ID();
			m_uniqueName += "#" + id;
		}
	} // makeUnique

	public String getBPLocName(MLocationExt address) {
		m_uniqueName = getName();
		m_unique = MSysConfig.getIntValue(MSysConfig.START_VALUE_BPLOCATION_NAME, 0,
				getAD_Client_ID(), getAD_Org_ID());
		if (m_unique < 0 || m_unique > 4)
			m_unique = 0;
		if (m_uniqueName != null) { // && m_uniqueName.equals(".")) {
			// default
			m_uniqueName = null;
			makeUnique(address);
		}

		// Check uniqueness
		MBPartnerLocationExt[] locations = getForBPartner(getCtx(),
				getC_BPartner_ID());
		boolean unique = locations.length == 0;
		while (!unique) {
			unique = true;
			for (int i = 0; i < locations.length; i++) {
				MBPartnerLocationExt location = locations[i];
				if (location.getC_BPartner_Location_ID() == get_ID())
					continue;
				if (m_uniqueName.equals(location.getName())) {
					// m_uniqueName = null;
					m_unique++;
					makeUnique(address);
					unique = false;
					break;
				}
			}
		}
		return m_uniqueName.toString();
	}

} // MBpartnerLocationExt

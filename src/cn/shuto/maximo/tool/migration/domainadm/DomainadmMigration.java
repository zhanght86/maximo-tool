package cn.shuto.maximo.tool.migration.domainadm;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cn.shuto.maximo.tool.migration.domainadm.bean.MaxDomain;
import cn.shuto.maximo.tool.system.SystemEnvironmental;
import cn.shuto.maximo.tool.util.CommonUtil;
import cn.shuto.maximo.tool.util.DBUtil;
import cn.shuto.maximo.tool.util.SerializeUtil;

public class DomainadmMigration {
	private static Logger _log = Logger.getLogger(DomainadmMigration.class.getName());
	private String DOMAINADMFILEPATH = "\\package\\domainadm\\Domainadm.mtep";

	private Connection conn = null;

	private static final String SELECTMAXDOMAIN = "select DOMAINID, DESCRIPTION, DOMAINTYPE, MAXTYPE, LENGTH, SCALE, MAXDOMAINID, INTERNAL from MAXDOMAIN WHERE DOMAINID = ?";

	PreparedStatement maxdomainST = null;

	Statement insertSt = null;

	public DomainadmMigration() {
		conn = DBUtil.getInstance()
				.getMaximoConnection(SystemEnvironmental.getInstance().getStringParam("-maximopath"));
		if (conn != null) {
			try {
				maxdomainST = conn.prepareStatement(SELECTMAXDOMAIN);

				insertSt = conn.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 导入域信息
	 */
	public void importDomainadm() {
		try {
			List<MaxDomain> list = SerializeUtil.readObjectForList(
					new File(SystemEnvironmental.getInstance().getStringParam("-importpath") + DOMAINADMFILEPATH));
			for (MaxDomain maxDomain : list) {
				insertSt.addBatch(maxDomain.toInsertSql());
			}
			
			insertSt.executeBatch();
			//提交事务
			conn.commit();
		} catch (SQLException e) {
			try {
				//回滚
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			closeResource();
		}
	}

	public void exportDomainadm(String exportObjects) {
		_log.info("---------- 需要导出的域为:" + exportObjects);
		// 需要导出的对象数组
		exportDomainadm(CommonUtil.buildExportObjects(exportObjects));
		
	}

	/**
	 * 导出域数据
	 * 
	 * @param exportdomains
	 */
	public void exportDomainadm(String[] exportdomains) {
		List<MaxDomain> list = new ArrayList<MaxDomain>();
		try {
			for (String domain : exportdomains) {
				list.add(exportMaxDomain(domain.toUpperCase()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResource();
		}

		// 将导出的集合进行Java序列化
		SerializeUtil.writeObject(list,
				new File(SystemEnvironmental.getInstance().getStringParam("-packagepath") + DOMAINADMFILEPATH));

	}

	/**
	 * 导出 MaxDomain 表及相关表数据
	 * 
	 * @param domainid
	 * @return
	 * @throws SQLException
	 */
	private MaxDomain exportMaxDomain(String domainid) throws SQLException {
		_log.info("--导出域--：" + domainid);
		// 导出maxdomain表
		MaxDomain maxDomain = exportMaxDomainToJavaBean(domainid);
		String domaintype = maxDomain.getDomaintype();
		if("字母数字".equals(domaintype)){
			
		}
		return maxDomain;
	}

	private MaxDomain exportMaxDomainToJavaBean(String domainid) throws SQLException {
		maxdomainST.setString(1, domainid);
		ResultSet rs = maxdomainST.executeQuery();
		if (rs.next()) {
			return new MaxDomain(CommonUtil.NULLTOEMPTY(rs.getString(1)), CommonUtil.NULLTOEMPTY(rs.getString(2)),
					CommonUtil.NULLTOEMPTY(rs.getString(3)), CommonUtil.NULLTOEMPTY(rs.getString(4)), rs.getInt(5),
					rs.getInt(6), rs.getInt(8));
		}
		rs.close();
		return null;
	}

	/**
	 * 关闭打开的资源
	 */
	private void closeResource() {
		try {
			if (maxdomainST != null)
				maxdomainST.close();

			if (insertSt != null)
				insertSt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
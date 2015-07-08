package com.github.reportengine.util;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * 可以对List<Map>结构的数据，进行sql语句查询，底层使用H2数据库，支持任何的标准SQL语句
 * @author badqiu
 *
 */
public class ObjectSqlQueryUtil {
	private static Logger log = LoggerFactory.getLogger(ObjectSqlQueryUtil.class);
	public static String TABLE_NAME = "t";
	
	public static List<Map<String,Object>> query(final String sql,final List<Map<String,Object>> rows) {
		return query(sql,rows,Collections.EMPTY_MAP);
	}
	
	public static List<Map<String,Object>> query(final String sql,final List<Map<String,Object>> rows,final Map params) {
		if(rows == null) return null;
		if(rows.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		final DataSource ds = getDataSource();
		TransactionTemplate tt = new TransactionTemplate(new DataSourceTransactionManager(ds));
		return tt.execute(new TransactionCallback<List<Map<String,Object>>>() {
			public List<Map<String,Object>> doInTransaction(TransactionStatus status) {
				createTableAndInsertData(TABLE_NAME,rows,ds);
				final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
				jdbcTemplate.execute("CREATE AGGREGATE IF NOT EXISTS collect_map FOR \"com.github.reportengine.h2.functions.CollectMapAggrFunction\"");
				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS map FOR \"com.github.reportengine.h2.functions.H2Functions.string_map\"");
				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS string_map FOR \"com.github.reportengine.h2.functions.H2Functions.string_map\"");
				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS number_map FOR \"com.github.reportengine.h2.functions.H2Functions.number_map\"");
				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS date_map FOR \"com.github.reportengine.h2.functions.H2Functions.date_map\"");
				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS get_property FOR \"com.github.reportengine.h2.functions.H2Functions.get_property\"");
				
				final NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
				return MapUtil.allMapKey2LowerCase(namedJdbcTemplate.queryForList(sql,params));
			}
		});
		
	}

//	public static List<Map<String,Object>> query(final String sql,final List<Map<String,Object>>... multiDataRows) {
//		if(multiDataRows == null) return null;
//		
//		final DataSource ds = getDataSource();
//		TransactionTemplate tt = new TransactionTemplate(new DataSourceTransactionManager(ds));
//		return tt.execute(new TransactionCallback<List<Map<String,Object>>>() {
//			public List<Map<String,Object>> doInTransaction(TransactionStatus status) {
//				int count = 0;
//				for(List<Map<String,Object>> rows : multiDataRows) {
//					createTableAndInsertData(TABLE_NAME+(++count),rows,ds);
//				}
//				final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
//				jdbcTemplate.execute("CREATE AGGREGATE IF NOT EXISTS collect_map FOR \"com.github.reportengine.h2.functions.CollectMapAggrFunction\"");
//				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS map FOR \"com.github.reportengine.h2.functions.H2Functions.string_map\"");
//				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS string_map FOR \"com.github.reportengine.h2.functions.H2Functions.string_map\"");
//				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS number_map FOR \"com.github.reportengine.h2.functions.H2Functions.number_map\"");
//				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS date_map FOR \"com.github.reportengine.h2.functions.H2Functions.date_map\"");
//				jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS get_property FOR \"com.github.reportengine.h2.functions.H2Functions.get_property\"");
//				
//				return MapUtil.allMapKey2LowerCase(jdbcTemplate.queryForList(sql));
//			}
//		});
//		
//	}
	
	private static void createTableAndInsertData(String tableName,final List<Map<String, Object>> rows,final DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String createTableSql = buildCreateTableSql(tableName, rows);
		Map row = rows.get(0);
		String insertSql = buildInsertSql(tableName,row);
		
		long start = System.currentTimeMillis();
		try {
			jdbcTemplate.execute(createTableSql);
			new NamedParameterJdbcTemplate(jdbcTemplate).batchUpdate(insertSql, rows.toArray(new Map[rows.size()]));
		}catch(Exception e) {
			throw new RuntimeException("error on createTableAndInsertData() createTableSql:"+createTableSql+" insertSql:"+insertSql,e) ;
		}
		long cost = System.currentTimeMillis() - start;
		if(cost > 1500) {
			log.warn("insert data into h2 db cost time:"+cost+", insert rows:"+rows.size()+", tps:"+(rows.size() * 1000.0 / cost));
		}
	}
	
	static String buildCreateTableSql(String tableName, List<Map<String, Object>> rows) {
		Map<String,Object> map = rows.get(0);
		StringBuilder sql = new StringBuilder("create memory local temporary  table "+tableName+" (");
		boolean first = true;
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			if(first) {
				first = false;
			}else {
				sql.append(",");
			}
			String key = entry.getKey();
			String sqlType = getSqlType(rows,key);
			sql.append(key + " " +sqlType);
		}
		
		return sql.append(" ) NOT PERSISTENT").toString();
	}
	
	private static String getSqlType(List<Map<String,Object>> rows,String key) {
		for(Map row : rows) {
			Object value = row.get(key);
			if(value == null) {
				continue;
			}
			return getSqlType(value);
		}
		return getSqlType(null);
	}
	
	private static String getSqlType(Object value) {
		if(value instanceof String) {
			return "varchar(4000)";
		}else if(value instanceof Integer) {
			return "BIGINT";
		}else if(value instanceof Long) {
			return "BIGINT";			
		}else if(value instanceof Number) {
			return "DOUBLE";
		}else if(value instanceof java.sql.Date) {
			return "date";
		}else if(value instanceof java.sql.Time) {
			return "time";
		}else if(value instanceof java.sql.Timestamp) {
			return "timestamp";
		}else if(value instanceof Date) {
			return "datetime";			
		}else if(value instanceof Boolean) {
			return "bool";
		}else {
			return "OTHER";
		}
	}

	static String buildInsertSql(String tableName,Map<String,Object> map) {
		StringBuilder sql = new StringBuilder("insert into "+tableName+" (");
		boolean first = true;
		for(String key : map.keySet()) {
			if(first) {
				first = false;
			}else {
				sql.append(",");
			}
			sql.append(key);
		}
		
		sql.append(" ) values (");
		
		first = true;
		for(String key : map.keySet()) {
			if(first) {
				first = false;
			}else {
				sql.append(",");
			}
			sql.append(":").append(key);
		}
		sql.append(" )");
		return sql.toString();
	}
//	private static AtomicLong dbCount = new AtomicLong();
	private static DataSource getDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setPassword("sa");
		ds.setPassword("");
//		ds.setUrl("jdbc:h2:mem:object_sql_query"+(dbCount.incrementAndGet())+";MODE=MYSQL");
		ds.setUrl("jdbc:h2:mem:object_sql_query;MODE=MYSQL;DB_CLOSE_DELAY=-1");
		return ds;
	}
}

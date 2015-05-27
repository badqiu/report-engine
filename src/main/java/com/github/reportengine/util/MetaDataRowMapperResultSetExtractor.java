package com.github.reportengine.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
/**
 * 抽取数据外，附加抽取ResultSetMetaData
 * 
 * @author badqiu
 *
 * @param <T>
 */
public class MetaDataRowMapperResultSetExtractor<T> extends RowMapperResultSetExtractor<T>{

	private List<ResultSetMetaDataInfo> metaDatas = null;
	
	public MetaDataRowMapperResultSetExtractor(RowMapper<T> rowMapper) {
		super(rowMapper);
	}
	
	public List<ResultSetMetaDataInfo> getMetaDatas() {
		return metaDatas;
	}

	@Override
	public List<T> extractData(ResultSet rs) throws SQLException {
		metaDatas = extractMetaData(rs);
		return super.extractData(rs);
	}

	private List<ResultSetMetaDataInfo> extractMetaData(ResultSet rs) throws SQLException {
		List<ResultSetMetaDataInfo> metaDatas = new ArrayList<ResultSetMetaDataInfo>();
		ResultSetMetaData metaData = rs.getMetaData();
		for(int column = 1; column <= metaData.getColumnCount(); column++) {
			ResultSetMetaDataInfo qmd = new ResultSetMetaDataInfo();
			qmd.setCatalogName(metaData.getCatalogName(column));
			qmd.setColumnClassName(metaData.getColumnClassName(column));
			qmd.setColumnDisplaySize(metaData.getColumnDisplaySize(column));
			qmd.setColumnLabel(metaData.getColumnLabel(column));
			qmd.setColumnName(metaData.getColumnName(column));
			qmd.setColumnType(metaData.getColumnType(column));
			qmd.setColumnTypeName(metaData.getColumnTypeName(column));
			qmd.setPrecision(metaData.getPrecision(column));
			qmd.setScale(metaData.getScale(column));
			qmd.setSchemaName(metaData.getSchemaName(column));
			qmd.setTableName(metaData.getTableName(column));
			metaDatas.add(qmd);
		}
		return metaDatas;
	}
}


package cn.voicecomm.ai.voicesagex.console.util.handler;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * PostgreSQL数组类型处理器，支持int、long、string数组（包括基本类型和包装类型）
 *
 * @author wangf
 */
@MappedTypes({int[].class, Integer[].class, long[].class, Long[].class, String[].class})
@MappedJdbcTypes(JdbcType.ARRAY)
@Slf4j
public class PgArrayTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        Connection connection = ps.getConnection();
        Array array = null;

        if (parameter instanceof int[] param) {
          Integer[] value = new Integer[param.length];
            for (int idx = 0; idx < param.length; idx++) {
                value[idx] = param[idx];
            }
            array = connection.createArrayOf("int4", value);
        } else if (parameter instanceof Integer[]) {
            array = connection.createArrayOf("int4", (Integer[]) parameter);
        } else if (parameter instanceof long[] param) {
          Long[] value = new Long[param.length];
            for (int idx = 0; idx < param.length; idx++) {
                value[idx] = param[idx];
            }
            array = connection.createArrayOf("int8", value);
        } else if (parameter instanceof Long[]) {
            array = connection.createArrayOf("int8", (Long[]) parameter);
        } else if (parameter instanceof String[]) {
            array = connection.createArrayOf("varchar", (String[]) parameter);
        }

        if (array != null) {
            ps.setArray(i, array);
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        return convert(array);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        return convert(array);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        return convert(array);
    }

    /**
     * 转换SQL数组到Java数组
     * @param sqlArray SQL数组
     * @return Java数组对象
     */
    private Object convert(Array sqlArray) throws SQLException {

        if (sqlArray == null) {
            // 返回对应类型的空数组
            return new int[0]; // 默认返回int数组，但实际应该根据上下文确定类型
        }

        Object arrObject = sqlArray.getArray();
        if (arrObject == null) {
            // 返回对应类型的空数组
            return new int[0]; // 默认返回int数组，但实际应该根据上下文确定类型
        }

        // 获取数据库列类型以确定如何处理
        String baseTypeName = sqlArray.getBaseTypeName();

        if ("int4".equalsIgnoreCase(baseTypeName)) {
            // 处理整数数组
            Object[] objArray = (Object[]) arrObject;
            Integer[] integerArray = new Integer[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                integerArray[i] = (Integer) objArray[i];
            }
            // 转换为基本类型数组
            int[] primitiveArray = new int[integerArray.length];
            for (int i = 0; i < integerArray.length; i++) {
                primitiveArray[i] = integerArray[i] != null ? integerArray[i] : 0;
            }
            return primitiveArray;
        } else if ("int8".equalsIgnoreCase(baseTypeName)) {
            // 处理长整型数组
            Object[] objArray = (Object[]) arrObject;
            Long[] longArray = new Long[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                longArray[i] = (Long) objArray[i];
            }
            // 转换为基本类型数组
            long[] primitiveArray = new long[longArray.length];
            for (int i = 0; i < longArray.length; i++) {
                primitiveArray[i] = longArray[i] != null ? longArray[i] : 0L;
            }
            return primitiveArray;
        } else if ("varchar".equalsIgnoreCase(baseTypeName) || "text".equalsIgnoreCase(baseTypeName)) {
            // 处理字符串数组
            Object[] objArray = (Object[]) arrObject;
            String[] strArray = new String[objArray.length];
            for (int i = 0; i < objArray.length; i++) {
                strArray[i] = (String) objArray[i];
            }
            return strArray;
        }

        return arrObject;
    }
}
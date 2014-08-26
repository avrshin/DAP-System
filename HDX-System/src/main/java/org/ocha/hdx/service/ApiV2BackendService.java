/**
 *
 */
package org.ocha.hdx.service;

import java.util.List;

import org.ocha.hdx.model.api2.ApiIndicatorValue;
import org.ocha.hdx.model.api2.ApiResultWrapper;

/**
 * @author alexandru-m-g
 *
 */
public interface ApiV2BackendService {

	/**
	 *
	 * @param indicatorTypeCodes list of indicator type codes by which the results should be filtered
	 * @param sourceCodes list of source codes by which the results should be filtered
	 * @param entityCodes list of location codes by which the results should be filtered
	 * @param startYear any indicators that are before this year will be filtered out
	 * @param endYear any indicators that are after this year will be filtered out
	 * @param lang the language in which the names should be represented, by default "en"
	 * @return a list of ApiIndicatorValue wrapped with information about results,pagination,etc
	 */
	ApiResultWrapper<ApiIndicatorValue>  listIndicatorsByCriteriaWithPagination(List<String> indicatorTypeCodes,
			List<String> sourceCodes, List<String> entityCodes,
			Integer startYear, Integer endYear, String periodType,
			Integer pageNum, Integer pageSize, String lang);

	/**
	 *
	 * @param indicatorTypeCodes list of indicator type codes by which the results should be filtered
	 * @param sourceCodes list of source codes by which the results should be filtered
	 * @param entityCodes list of location codes by which the results should be filtered
	 * @param startYear any indicators that are before this year will be filtered out
	 * @param endYear any indicators that are after this year will be filtered out
	 * @param pageNum for pagination, the page number (starting from 0) that needs to be returned,
	 * @param pageSize for pagination, the number of items that are present in a page,
	 * @param lang the language in which the names should be represented, by default "en"
	 * @return a list of ApiIndicatorValue wrapped with information about results,pagination,etc
	 */
	ApiResultWrapper<ApiIndicatorValue>  listIndicatorsByCriteria(List<String> indicatorTypeCodes,
			List<String> sourceCodes, List<String> entityCodes,
			Integer startYear, Integer endYear, final String periodType, String lang);
}

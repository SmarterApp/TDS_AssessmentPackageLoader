import { SortResponse } from "./sort-response";

/**
 * An interface that defines a "page" of data from the server.  This interface mimics what is returned by Spring Data's
 * {Page} class.
 *
 * @see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
 * @see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Slice.html
 */
export interface PageResponse<T> {
  /**
   * The records that make up the page of data.  These are the records that are intended for display.
   */
  content: T[];

  /**
   * The number of elements in this page that satisfied the query.
   * <p>
   *     While this value could be determined by getting the length of the {content} array, it's already returned by
   *     Spring Data, so there's no need to calculate it again.
   * </p>
   */
  numberOfElements: number;

  /**
   * Indicates if this page is the first page.
   */
  first: boolean;

  /**
   * Indicates if this page is the last page.
   */
  last: boolean;

  /**
   * The total number of pages that satisfy the query.
   */
  totalPages: number;

  /**
   * The total number of elements across all pages that satisfy the query
   */
  totalElements: number;

  /**
   * The total number of elements that can be in this page.
   */
  size: number;

  /**
   * The ordinal position of this page in the total number of pages.  Zero-based, always non-negative.
   */
  number: number;

  /**
   * A collection of sorting information.
   * <p>
   *     This is an array to allow for multiple sort criteria (e.g. first name then last name).
   * </p>
   */
  sort: SortResponse[]
}

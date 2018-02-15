import { SortDirection } from "./sort-direction.enum";

/**
 * An interface representing the sorting information returned from Spring Data.
 *
 * @see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Sort.html
 */
export interface SortResponse {
  /**
   * The {SortDirection} of the specified property, either "ASC" or "DESC".
   */
  direction: SortDirection;

  /**
   * The name of the property being sorted
   */
  property: string;

  /**
   * Indicates if the sort is case-sensitive
   */
  ignoreCase: boolean;

  /**
   * null handling hints that can be used in Sort.Order expressions.
   */
  nullHandling: string;

  /**
   * Indicates the sort is in ascending order
   */
  ascending: boolean;

  /**
   * Indicates the sort is in descending order
   */
  descending: boolean;
}

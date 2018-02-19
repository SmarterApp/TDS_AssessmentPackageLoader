import { SortDirection } from "./sort-direction.enum";

/**
 * An interface for describing pagination data sent to/from the server.
 */
export interface PageRequest {
  /**
   * The zero-based index of the page
   */
  page: number;

  /**
   * The number of records included in each page
   */
  size: number;

  /**
   * the property to sort on
   */
  sort: string;

  /**
   * Whether the sort order should be ascending or descending
   */
  sortDir: SortDirection
}

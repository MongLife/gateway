/** 백엔드 com.monglife.core.dto.response.ResponseDto 와 1:1 */
export interface ResponseDto<T> {
  code: string;
  message: string;
  result: T;
}

/** com.monglife.core.dto.response.PageResponseDto */
export interface PageResponseDto<T> extends ResponseDto<T> {
  page: number;
  size: number;
  totalPage?: number;
  isLastPage?: boolean;
}

export interface PageParams {
  [key: string]: string | number | boolean | undefined | null;
  page?: number;
  size?: number;
  query?: string;
}

/** 클라이언트에서 쓰는 페이지 결과. total 은 헤더/응답에 없으면 목이 채운다. */
export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  total: number;
}

export type SortDir = 'asc' | 'desc';
export interface SortState {
  key: string;
  dir: SortDir;
}
/** Spring Pageable 스타일 `sort=field,desc` */
export const toSortParam = (sort?: SortState | null) => (sort ? `${sort.key},${sort.dir}` : undefined);

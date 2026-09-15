import { useQuery } from '@tanstack/react-query';
import { metaApi } from './api';

export const useFilterOptions = () =>
  useQuery({ queryKey: ['meta', 'filters'], queryFn: metaApi.filters, staleTime: 5 * 60_000 });

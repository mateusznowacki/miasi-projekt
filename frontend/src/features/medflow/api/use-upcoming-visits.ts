import { useQuery } from "@tanstack/react-query";
import { fetchUpcomingVisits } from "./fetch-upcoming-visits";

export function useUpcomingVisits() {
  return useQuery({
    queryKey: ["medflow", "upcoming-visits"],
    queryFn: fetchUpcomingVisits,
  });
}

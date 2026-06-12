import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updatePersonalData, type UpdatePersonalDataReq } from "@/client";

export function useUpdatePatientPersonal(patientId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (body: UpdatePersonalDataReq) => {
      const { data } = await updatePersonalData({
        path: { patientId },
        body,
        throwOnError: true,
      });
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["patients"] });
    },
  });
}

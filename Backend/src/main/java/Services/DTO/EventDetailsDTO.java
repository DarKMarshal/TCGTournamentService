package Services.DTO;

import java.util.List;

/**
 * Full event detail including all divisions and their results.
 */
public record EventDetailsDTO(String id, String name, List<DivisionDTO> divisions) {}

package server.dto;
import lombok.Data;

@Data
public class LabelDTO {

    private Long id;
    private Long[] ids;
    private String title;
    private UserDTO createdBy;
    private String createdAt;
}

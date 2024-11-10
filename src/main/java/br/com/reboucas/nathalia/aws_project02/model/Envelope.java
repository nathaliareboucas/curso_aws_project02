package br.com.reboucas.nathalia.aws_project02.model;

import br.com.reboucas.nathalia.aws_project02.enums.EventType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envelope {
    private EventType eventType;
    private String data;
}

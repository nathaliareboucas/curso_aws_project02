package br.com.reboucas.nathalia.aws_project02.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    private Long productId;
    private String code;
    private String username;
}

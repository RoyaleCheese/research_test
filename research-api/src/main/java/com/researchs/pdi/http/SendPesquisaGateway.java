package com.researchs.pdi.http;

import com.researchs.pdi.dto.EntrevistadoReceiveDTO;
import com.researchs.pdi.dto.PerguntasERespostasDTO;
import com.researchs.pdi.dto.PesquisaDTO;
import com.researchs.pdi.models.Entrevistado;
import com.researchs.pdi.models.Pergunta;
import com.researchs.pdi.models.Pesquisa;
import com.researchs.pdi.services.EntrevistadoService;
import com.researchs.pdi.services.FolhaService;
import com.researchs.pdi.services.PerguntaService;
import com.researchs.pdi.services.PesquisaService;
import com.researchs.pdi.services.RespostaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import static com.researchs.pdi.utils.DateUtils.getDate;
import static com.researchs.pdi.utils.DateUtils.getParse;

@RequiredArgsConstructor
@Component
public class SendPesquisaGateway {

    private static final String PESQUISA = "PESQUISA TESTE";
    private static final Date DATA = getDate(getParse("20/11/2016"));

    private final PesquisaService pesquisaService;
    private final PerguntaService perguntaService;
    private final RespostaService respostaService;
    private final FolhaService folhaService;
    private final EntrevistadoService entrevistadoService;

    public PesquisaDTO getEstruturaBasica(HttpServletRequest request) {
        Pesquisa pesquisa = pesquisaService.pesquisa(PESQUISA, DATA);
        return getPesquisaDTO(pesquisa);
    }

    private PesquisaDTO getPesquisaDTO(Pesquisa pesquisa) {
        return PesquisaDTO.builder()
                .pesquisa(pesquisa)
                .perguntasERespostas(getPerguntasERespostas(pesquisa))
                .folhas(folhaService.pesquisa(pesquisa))
                .build();
    }

    private ArrayList<PerguntasERespostasDTO> getPerguntasERespostas(Pesquisa pesquisa) {
        return perguntaService.pesquisa(pesquisa)
                .stream()
                .map(this::build)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private PerguntasERespostasDTO build(Pergunta pergunta) {
        return PerguntasERespostasDTO.builder()
                .pergunta(pergunta)
                .respostas(respostaService.pesquisa(pergunta))
                .build();
    }

    public void initDB(HttpServletRequest request) {
        pesquisaService.apagarTodasAsPesquisas();

        Pesquisa pesquisa = pesquisaService.novo(PESQUISA, getDate(getParse("20/11/2016")));
        Pergunta pergunta = perguntaService.novo(pesquisa, 1, "Faixa et??ria");
        respostaService.novo(pergunta, "a", "Entre 16 e 21 anos");
        respostaService.novo(pergunta, "b", "Entre 22 e 28 anos");
        respostaService.novo(pergunta, "c", "Entre 29 e 40 anos");
        respostaService.novo(pergunta, "d", "Entre 41 e 59 anos");
        respostaService.novo(pergunta, "e", "Acima de 60 anos");

        pergunta = perguntaService.novo(pesquisa, 2, "Escolaridade");
        respostaService.novo(pergunta, "a", "Analfabeto");
        respostaService.novo(pergunta, "b", "Fundamental inicial incompleto");
        respostaService.novo(pergunta, "c", "Fundamental inicial completo");
        respostaService.novo(pergunta, "d", "Fundamental final incompleto");
        respostaService.novo(pergunta, "e", "Fundamental final completo");
        respostaService.novo(pergunta, "f", "Ensino m??dio incompleto");
        respostaService.novo(pergunta, "g", "Ensino m??dio completo");
        respostaService.novo(pergunta, "h", "Superior incompleto");
        respostaService.novo(pergunta, "i", "Superior completo");

        pergunta = perguntaService.novo(pesquisa, 3, "Sexo");
        respostaService.novo(pergunta, "a", "Masculino");
        respostaService.novo(pergunta, "b", "Feminino");

        pergunta = perguntaService.novo(pesquisa, 4, "Renda Familiar");
        respostaService.novo(pergunta, "a", "At?? 1 sal??rio m??nimo");
        respostaService.novo(pergunta, "b", "Entre 1 e 5 sal??rios m??nimos");
        respostaService.novo(pergunta, "c", "Entre 6 e 9 sal??rios m??nimos");
        respostaService.novo(pergunta, "d", "Entre 10 e 19 sal??rios m??nimos");
        respostaService.novo(pergunta, "e", "Acima de 19 sal??rios m??nimos");

        pergunta = perguntaService.novo(pesquisa, 5, "Mesorregi??o");
        respostaService.novo(pergunta, "a", "A");
        respostaService.novo(pergunta, "b", "B");
        respostaService.novo(pergunta, "c", "C");
        respostaService.novo(pergunta, "d", "D");
        respostaService.novo(pergunta, "e", "E");

        pergunta = perguntaService.novo(pesquisa, 6, "Em quem voc?? votaria");
        respostaService.novo(pergunta, "a", "Candidato A");
        respostaService.novo(pergunta, "b", "Candidato B");
        respostaService.novo(pergunta, "c", "Candidato C");
        respostaService.novo(pergunta, "d", "Candidato D");
        respostaService.novo(pergunta, "e", "Nenhum / N??o sabe / N??o opinou");

        pergunta = perguntaService.novo(pesquisa, 7, "Em quem voc?? n??o votaria");
        respostaService.novo(pergunta, "a", "Candidato A");
        respostaService.novo(pergunta, "b", "Candidato B");
        respostaService.novo(pergunta, "c", "Candidato C");
        respostaService.novo(pergunta, "d", "Candidato D");
        respostaService.novo(pergunta, "e", "Nenhum / N??o sabe / N??o opinou");

        folhaService.criarFolhas(pesquisa, 3);
    }

    public void salvar(EntrevistadoReceiveDTO dto) throws Exception {
        final Pesquisa pesquisa = pesquisaService.pesquisa(dto.getPesquisa());
        entrevistadoService.salvar(build(dto, pesquisa));
    }

    private Entrevistado build(EntrevistadoReceiveDTO dto, Pesquisa pesquisa) {
        return Entrevistado.builder()
                .pesquisa(pesquisa)
                .folha(folhaService.pesquisa(pesquisa, dto.getFolha()))
                .pergunta(perguntaService.pesquisa(pesquisa, dto.getPergunta()))
                .resposta(respostaService.pesquisa(pesquisa, dto.getPergunta(), dto.getOpcaoResposta()))
                .build();
    }

}

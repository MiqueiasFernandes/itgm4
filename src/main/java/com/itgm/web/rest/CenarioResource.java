package com.itgm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.itgm.domain.Cenario;

import com.itgm.domain.Projeto;
import com.itgm.repository.CenarioRepository;
import com.itgm.service.jriaccess.Itgmrest;
import com.itgm.web.rest.util.HeaderUtil;
import com.itgm.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Cenario.
 */
@RestController
@RequestMapping("/api")
public class CenarioResource {

    private final Logger log = LoggerFactory.getLogger(CenarioResource.class);

    private static final String ENTITY_NAME = "cenario";

    private final CenarioRepository cenarioRepository;

    public CenarioResource(CenarioRepository cenarioRepository) {
        this.cenarioRepository = cenarioRepository;
    }

    /**
     * POST  /cenarios : Create a new cenario.
     *
     * @param cenario the cenario to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cenario, or with status 400 (Bad Request) if the cenario has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cenarios")
    @Timed
    public ResponseEntity<Cenario> createCenario(@RequestBody Cenario cenario) throws URISyntaxException {
        log.debug("REST request to save Cenario : {}", cenario);
        if (cenario.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new cenario cannot already have an ID")).body(null);
        }

        if (cenario.getProjeto() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "invaliduser", "Informe o projeto para criar o novo cenario.")).body(null);
        }

        Cenario result = cenarioRepository.save(cenario);

        Projeto projeto = result.getProjeto();

        if (Itgmrest.createNewFile(
            projeto.getUser().getLogin(),
            projeto.getNome(),
            result.getNome(),
            "*",
            null,
            ".info",
            result.toString())
            ) {
            if (
                Itgmrest.listFiles(
                    projeto.getUser().getLogin(),
                    projeto.getNome(),
                    result.getNome(),
                    "*")
                .contains(projeto.getUser().getLogin() + "/" + projeto.getNome() + "/" + result.getNome() + "/" + ".info")) {
                result.setCaminho(
                    projeto.getUser().getLogin() + "/" + projeto.getNome() + "/" + result.getNome() + "/"
                );
                result = updateCenario(result).getBody();
            } else {
                return ResponseEntity
                    .badRequest()
                    .headers(
                        HeaderUtil
                            .createFailureAlert(
                                ENTITY_NAME,
                                "ITGMRestfalhou",
                                "Erro ao tentar criar novo cenario."))
                    .body(null);
            }
        }
        return ResponseEntity.created(new URI("/api/cenarios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cenarios : Updates an existing cenario.
     *
     * @param cenario the cenario to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cenario,
     * or with status 400 (Bad Request) if the cenario is not valid,
     * or with status 500 (Internal Server Error) if the cenario couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cenarios")
    @Timed
    public ResponseEntity<Cenario> updateCenario(@RequestBody Cenario cenario) throws URISyntaxException {
        log.debug("REST request to update Cenario : {}", cenario);
        if (cenario.getId() == null) {
            return createCenario(cenario);
        }
        Cenario result = cenarioRepository.save(cenario);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cenario.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cenarios : get all the cenarios.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cenarios in body
     */
    @GetMapping("/cenarios")
    @Timed
    public ResponseEntity<List<Cenario>> getAllCenarios(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Cenarios");
        Page<Cenario> page;

//        if(SecurityUtils.isCurrentUserInRole("ROLE_ADMIN"))
//            page = cenarioRepository.findAll(pageable);
//        else
            page = cenarioRepository.findByUserIsCurrentUser(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cenarios");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cenarios/:id : get the "id" cenario.
     *
     * @param id the id of the cenario to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cenario, or with status 404 (Not Found)
     */
    @GetMapping("/cenarios/{id}")
    @Timed
    public ResponseEntity<Cenario> getCenario(@PathVariable Long id) {
        log.debug("REST request to get Cenario : {}", id);
        Cenario cenario = cenarioRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(cenario));
    }

    /**
     * DELETE  /cenarios/:id : delete the "id" cenario.
     *
     * @param id the id of the cenario to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cenarios/{id}")
    @Timed
    public ResponseEntity<Void> deleteCenario(@PathVariable Long id) {
        log.debug("REST request to delete Cenario : {}", id);
        Cenario cenario = getCenario(id).getBody();
        Projeto projeto = cenario.getProjeto();
        Itgmrest.removeDIR(
           projeto.getUser().getLogin(),
            projeto.getNome(),
            cenario.getNome(),
            null,
            null,
            null);
        cenarioRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }


    /**
     * GET  /listar/:id : get list of files in cenario.
     *
     * @param id the id of the cenario to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of files in body
     */
    @GetMapping("/cenarios/listar/{id}")
    @Timed
    public ResponseEntity<String> getAllFilesName(@PathVariable Long id) {
        Cenario cenario = cenarioRepository.findOne(id);
        log.debug("REST request to get a list of files in Cenario " + cenario.getId());

        return new ResponseEntity<>("{\"files\":\"" + Itgmrest.listFiles(
            cenario.getProjeto().getUser().getLogin(),
            cenario.getProjeto().getNome(),
            cenario.getNome(),
            "*"
        ) + "\"}", HttpStatus.OK);
    }

    @GetMapping("/cenarios/publicar/{id}")
    @Timed
    public ResponseEntity<String> publicarArquivoparaExibir(
        @PathVariable Long id,
        @RequestParam("diretorio") String diretorio,
        @RequestParam("subdiretorio") String subdiretorio,
        @RequestParam("file") String file,
        @RequestParam("meta") boolean meta,
        @RequestParam("content") boolean conteudo,
        @RequestParam("cript") boolean cript,
        @RequestParam("image") boolean image) {

        log.debug("REST request to public file in Cenario: " + id);
        Cenario cenario = cenarioRepository.findOne(id);
        return new ResponseEntity<String>("{\"file\":\"" +
            (conteudo ?
                Itgmrest.getContent(
                    cenario.getProjeto().getUser().getLogin(),
                    cenario.getProjeto().getNome(),
                    cenario.getNome(),
                    diretorio,
                    file,
                    subdiretorio,
                    cript,
                    "")
                :
                Itgmrest.publicFile(
                    cenario.getProjeto().getUser().getLogin(),
                    cenario.getProjeto().getNome(),
                    cenario.getNome(),
                    diretorio,
                    subdiretorio,
                    file,
                    meta,
                    image
                    )
            ) + "\"}", HttpStatus.OK);
    }
}

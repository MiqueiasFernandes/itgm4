package com.itgm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.itgm.domain.Base;

import com.itgm.repository.BaseRepository;
import com.itgm.security.SecurityUtils;
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
import org.springframework.web.multipart.MultipartFile;
import com.itgm.service.jriaccess.Itgmrest;
import com.itgm.domain.Projeto;

/**
 * REST controller for managing Base.
 */
@RestController
@RequestMapping("/api")
public class BaseResource {

    private final Logger log = LoggerFactory.getLogger(BaseResource.class);

    private static final String ENTITY_NAME = "base";

    private final BaseRepository baseRepository;

    public BaseResource(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;
    }

    /**
     * POST  /bases : Create a new base.
     *
     * @param base the base to create
     * @return the ResponseEntity with status 201 (Created) and with body the new base, or with status 400 (Bad Request) if the base has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bases")
    @Timed
    public ResponseEntity<Base> createBase(@RequestBody Base base) throws URISyntaxException {
        log.debug("REST request to save Base : {}", base);
        if (base.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new base cannot already have an ID")).body(null);
        }

        if (base.getProjeto() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "invaliduser", "Informe o projeto para criar o novo cenario.")).body(null);
        }

        Base result = baseRepository.save(base);

        Projeto projeto = result.getProjeto();

        if (Itgmrest.createNewFile(
            projeto.getUser().getLogin(),
            projeto.getNome(),
            "*",
            "*",
            "bases/" + result.getId() + "/",
            ".info",
            result.toString())) {
            if (
                Itgmrest.listFiles(
                    projeto.getUser().getLogin(),
                    projeto.getNome(),
                    "bases",
                    result.getId().toString()
                )
                    .contains(
                        projeto.getUser().getLogin() + "/" +
                            projeto.getNome() + "/" +
                            "bases/" +
                            result.getId() + "/" + ".info")
                ) {
                result.setLocal(
                    projeto.getUser().getLogin() + "/" +
                        projeto.getNome() + "/" +
                        "bases/" +
                        result.getId() + "/");
                result = updateBase(result).getBody();
            } else {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "ITGMRestfalhou", "Erro ao tentar criar nova base.")).body(null);
            }
        }

        return ResponseEntity.created(new URI("/api/bases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bases : Updates an existing base.
     *
     * @param base the base to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated base,
     * or with status 400 (Bad Request) if the base is not valid,
     * or with status 500 (Internal Server Error) if the base couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bases")
    @Timed
    public ResponseEntity<Base> updateBase(@RequestBody Base base) throws URISyntaxException {
        log.debug("REST request to update Base : {}", base);
        if (base.getId() == null) {
            return createBase(base);
        }
        Base result = baseRepository.save(base);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, base.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bases : get all the bases.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bases in body
     */
    @GetMapping("/bases")
    @Timed
    public ResponseEntity<List<Base>> getAllBases(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Bases");

        Page<Base> page;

//        if(SecurityUtils.isCurrentUserInRole("ROLE_ADMIN"))
//            page = baseRepository.findAll(pageable);
//        else
        page = baseRepository.findByUserIsCurrentUser(pageable);


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bases");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bases/:id : get the "id" base.
     *
     * @param id the id of the base to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the base, or with status 404 (Not Found)
     */
    @GetMapping("/bases/{id}")
    @Timed
    public ResponseEntity<Base> getBase(@PathVariable Long id) {
        log.debug("REST request to get Base : {}", id);
        Base base = baseRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(base));
    }

    /**
     * DELETE  /bases/:id : delete the "id" base.
     *
     * @param id the id of the base to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bases/{id}")
    @Timed
    public ResponseEntity<Void> deleteBase(@PathVariable Long id) {
        log.debug("REST request to delete Base : {}", id);
        Base base = getBase(id).getBody();
        Projeto projeto = base.getProjeto();
        Itgmrest.removeDIR(
            projeto.getUser().getLogin(),
            projeto.getNome(),
            "*",
            "*",
            base.getId() + "/",
            "bases/");

        baseRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @PostMapping("/bases/temp")
    public ResponseEntity<String> baseTempUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("usuario") String usuario,
        @RequestParam("projeto") String projeto,
        @RequestParam("nome") String nome,
        @RequestParam("id") String id
    ) {

        if (!Itgmrest.postBinario(
            usuario,
            projeto,
            "*",
            "*",
            nome,
            "bases/" + id + "-temp/",
            file
        )
            ) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        String codigo = "load('" + nome + "')\nwrite(gsub(\", \", \",\", " +
            "toString(subset((data.frame(l = sapply(ls(), " +
            "function(X){if (T %in% (c(\"data.table\", \"data.frame\") " +
            "%in% class(get(X)))) return(T) ; return(F)}), m = ls())), l == T)$m)), " +
            "\"variaveis\")";

        Itgmrest.executarBatch(usuario, projeto, "bases", id + "-temp/", codigo);

        return new ResponseEntity<String>(
            "{\"sucesso\":\"base enviada\"}", HttpStatus.OK);
    }

    @PostMapping("/bases/send")
    public ResponseEntity<String> baseUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("usuario") String usuario,
        @RequestParam("projeto") String projeto,
        @RequestParam("nome") String nome,
        @RequestParam("id") String id,
        @RequestParam("extra") String extra
    ) {
        String codigo;
        String diretorio = id;

        if(nome.endsWith(".csv")) {
            if (!Itgmrest.postBinario(
                usuario,
                projeto,
                "*",
                "*",
                nome,
                "bases/" + id + "/",
                file
            )) {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            codigo = "write(" +
                "gsub(', ', ',', " +
                "toString(" +
                "names(" +
                "read.csv('" + nome + "', sep='" + extra + "')))), 'campos')";
        } else {
            codigo = "load('../999-temp/" + nome + "')\n" +
                "write(gsub(', ', ',', toString(names(" + extra + "))), 'campos')\n" +
                "save.image(\"" + nome + "\")\n" +
                "save(\""+extra+"\",\"" + nome + extra + ".RData\")\n";
        }

        Itgmrest.executarBatch(usuario, projeto, "bases", id, codigo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000); ///so pode carregar bases apos este tempo
                    Itgmrest.removeDIR(
                        usuario,
                        projeto,
                        "*",
                        "*",
                        "*-temp/",
                        "bases/"
                    );
                }catch (Exception ex){

                }
            }
        }).start();


        return new ResponseEntity<String>(
            "{\"sucesso\":\"base enviada\"}", HttpStatus.OK);
    }

    @GetMapping("/bases/campos/{id}")
    @Timed
    public ResponseEntity<String> getBaseFields(@PathVariable Long id) {
        log.debug("REST request to get fields of Base : {}", id);
        Base base = baseRepository.findOne(id);
        return getCSV(
            base.getProjeto().getUser().getLogin(),
            base.getProjeto().getNome(),
            base.getId() + "/",
            "campos");
    }

    @GetMapping("/bases/variaveis/{usuario}/{projeto}/{id}")
    @Timed
    public ResponseEntity<String> getBaseVariaveis(
        @PathVariable String usuario,
        @PathVariable String projeto,
        @PathVariable String id
    ) {
        return getCSV(usuario, projeto, id + "-temp/", "variaveis");
    }

    public ResponseEntity<String> getCSV(String usuario,
                                         String projeto,
                                         String subdiretorio,
                                         String file){
        String arquivo = Itgmrest.getContent(
            usuario,
            projeto,
            "*",
            "*",
            file,
            "bases/" + subdiretorio,
            false,
            ""
        );

        if(arquivo != null && arquivo.length() > 0){
            arquivo = arquivo.replaceAll("\n", "");
        }
        if(arquivo == null || arquivo.isEmpty() || arquivo.startsWith("error:")){
            return new ResponseEntity<String>(
                "{\"error\":\"" + (arquivo != null ? arquivo.substring(6) : "") + "\"}",
                HttpStatus.OK);
        }
        String campos[] = arquivo.split(",");
        String saida = java.util.Arrays.toString(campos);
        saida = saida.replaceAll(", ", "\",\"");
        saida = saida.substring(1, saida.length() - 1);
        saida = "[\"" + saida + "\"]";
        return new ResponseEntity<String>(
            "{\""  +file+ "\":" + saida + "}", HttpStatus.OK);
    }

}

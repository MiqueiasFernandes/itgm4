import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { ModeloExclusivo } from './modelo-exclusivo.model';
import { ModeloExclusivoPopupService } from './modelo-exclusivo-popup.service';
import { ModeloExclusivoService } from './modelo-exclusivo.service';
import { Modelo, ModeloService } from '../modelo';
import { Cenario, CenarioService } from '../cenario';
import { Avaliacao, AvaliacaoService } from '../avaliacao';

@Component({
    selector: 'jhi-modelo-exclusivo-dialog',
    templateUrl: './modelo-exclusivo-dialog.component.html'
})
export class ModeloExclusivoDialogComponent implements OnInit {

    modeloExclusivo: ModeloExclusivo;
    authorities: any[];
    isSaving: boolean;

    modelos: Modelo[];

    cenarios: Cenario[];

    avaliacaos: Avaliacao[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private modeloExclusivoService: ModeloExclusivoService,
        private modeloService: ModeloService,
        private cenarioService: CenarioService,
        private avaliacaoService: AvaliacaoService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['modeloExclusivo']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.modeloService.query().subscribe(
            (res: Response) => { this.modelos = res.json(); }, (res: Response) => this.onError(res.json()));
        this.cenarioService.query().subscribe(
            (res: Response) => { this.cenarios = res.json(); }, (res: Response) => this.onError(res.json()));
        this.avaliacaoService.query().subscribe(
            (res: Response) => { this.avaliacaos = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.modeloExclusivo.id !== undefined) {
            this.modeloExclusivoService.update(this.modeloExclusivo)
                .subscribe((res: ModeloExclusivo) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.modeloExclusivoService.create(this.modeloExclusivo)
                .subscribe((res: ModeloExclusivo) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: ModeloExclusivo) {
        this.eventManager.broadcast({ name: 'modeloExclusivoListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    trackModeloById(index: number, item: Modelo) {
        return item.id;
    }

    trackCenarioById(index: number, item: Cenario) {
        return item.id;
    }

    trackAvaliacaoById(index: number, item: Avaliacao) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}

@Component({
    selector: 'jhi-modelo-exclusivo-popup',
    template: ''
})
export class ModeloExclusivoPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private modeloExclusivoPopupService: ModeloExclusivoPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.modeloExclusivoPopupService
                    .open(ModeloExclusivoDialogComponent, params['id']);
            } else {
                this.modalRef = this.modeloExclusivoPopupService
                    .open(ModeloExclusivoDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}

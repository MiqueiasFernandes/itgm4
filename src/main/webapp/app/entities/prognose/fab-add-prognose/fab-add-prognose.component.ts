import { Component, OnInit } from '@angular/core';

import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {AlertService} from 'ng-jhipster';
import {Customize, CustomizeService } from '../../customize/';
import { HomeService } from '../../../home/home.service';
import {PrognoseService} from '../prognose.service';
import {Prognose} from "../prognose.model";


@Component({
    selector: 'jhi-fab-add-prognose',
    templateUrl: './fab-add-prognose.component.html',
    styleUrls: [
        './fab-add-prognose.scss'
    ]
})
export class FabAddPrognoseComponent implements OnInit {


    prognose: Prognose;
    primeira = 1;
    ultima = 5;
    etapa = 1;
    atual = 1;
    feito = true;
    onLoad = false;
    modobases = 0;

    constructor(
        public activeModal: NgbActiveModal,
        private customizeService: CustomizeService,
        private alertService: AlertService,
        private prognoseService: PrognoseService,
        private homeService: HomeService,) {
        this.prognose = new Prognose();
    }

    ngOnInit() {
    }

    voltar() {
        this.atual = this.etapa = Math.max(this.primeira, --this.etapa);
    }

    avancar() {
        this.atual = this.etapa = this.feito ? Math.min(this.ultima, ++this.etapa) : this.atual;
    }

    goto(netapa: number) {
        if (this.etapa >= netapa) {
            this.atual = netapa;
        }
    }


    enviar() {
        alert('e');
        // this.onLoad = true;
        // this.customizeService.getCustomize()
        //     .subscribe((customize: Customize) => {
        //         if (customize && customize.cenario) {
        //
        //         }
        //     });
    }


    radiobases($event) {
        if ($event.target.checked) {
            switch ($event.target.id) {
                case 'uma':
                    this.modobases = 1;
                    break;
                case 'duas':
                    this.modobases = 2;
                    break;
            }
        }
    }

    private onError(error) {
        this.alertService.error(error.message);
    }

    private close() {
        this.activeModal.dismiss('closed');
    }

}

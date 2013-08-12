/* Frozen.js

	Purpose:

	Description:

	History:
		Wed Sep  2 10:07:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
/**
 * A frozen component to represent a frozen column or row in grid, like MS Excel. 
 * <p>Default {@link #getZclass}: z-frozen.
 */
zul.mesh.Frozen = zk.$extends(zul.Widget, {
	_start: 0,
	_scrollScale: 0,
	$define: {
    	/**
    	 * Returns the number of columns to freeze.
    	 * <p>Default: 0
    	 * @return int
    	 */
    	/**
    	 * Sets the number of columns to freeze.(from left to right)
    	 * @param int columns positive only
    	 */
		columns: [function(v) {
			return v < 0 ? 0 : v;
		}, function(v) {
			if (this._columns) {
				if (this.desktop) {
					this.onSize();
					this.syncScroll();
				}
			} else this.rerender();
		}],
		/**
		 * Returns the start position of the scrollbar.
		 * <p>Default: 0
		 * @return int
		 */
		/**
		 * Sets the start position of the scrollbar.
		 * <p> Default: 0
		 * @param int start the column number
		 */
		start: null
	},
	bind_: function () {
		this.$supers(zul.mesh.Frozen, 'bind_', arguments);
		var p = this.parent,
			body = p.$n('body'),
			foot = p.$n('foot');
		if (body)
			jq(body).addClass('z-word-nowrap');
		if (foot)
			jq(foot).addClass('z-word-nowrap');
	},
	unbind_: function () {
		var p = this.parent,
			body = p.$n('body'),
			foot = p.$n('foot');
		if (body = p.$n('body'))
			jq(body).removeClass('z-word-nowrap');
		if (foot)
			jq(foot).removeClass('z-word-nowrap');
		this.$supers(zul.mesh.Frozen, 'unbind_', arguments);
	},
	beforeParentChanged_: function (p) {
		//bug B50-ZK-238
		if (this._lastScale) //if large then 0
			this._doScroll(0);
		
		this.$supers('beforeParentChanged_', arguments);
	},
	_doScroll: function (num) {
		num = Math.ceil(num);
		if (this._lastScale == num)
			return;
		this._lastScale = num;
		this._doScrollNow(num);
		this.smartUpdate('start', num);
		this._start = num;
	},
	_doScrollNow: function (num, force) {
		var totalWidth = 0,
			mesh = this.parent,
			cnt = num,
			rows = mesh.ebodyrows;

		if (mesh.head) {
			totalWidth = mesh.eheadtbl.offsetWidth;
			// set fixed size
			var hdrows = mesh.eheadrows.rows,
				hdcells = mesh.eheadrows.rows[hdrows.length - 1].cells,
				hdcol = mesh.ehdfaker.firstChild,
				ftrows = mesh.foot ? mesh.efootrows : null,
				ftcells = ftrows ? ftrows.rows[0].cells : null;
			
			for (var faker, i = 0; hdcol; hdcol = hdcol.nextSibling) {
				if (hdcol.style.width.indexOf('px') == -1) {
					var sw = hdcol.style.width = jq.px0(hdcells[i].offsetWidth),
						wgt = zk.Widget.$(hdcol);
					if (!wgt.$instanceof(zul.mesh.HeadWidget)) {
						if ((faker = wgt.$n('bdfaker')))
							faker.style.width = sw;
						if ((faker = wgt.$n('ftfaker')))
							faker.style.width = sw;
					}
				}
				i++;
			}
			
			for (var i = this._columns, len = hdcells.length; i < len; i++) {
				var n = hdcells[i],
					hdWgt = zk.Widget.$(n),
					isVisible = hdWgt && hdWgt.isVisible(),
					shallUpdate = false,
					cellWidth;

				if (cnt-- <= 0) { //show
					var wd = n.offsetWidth;
					if (force || wd == 0 || wd == 1) {
						cellWidth = hdWgt._origWd || jq.px(wd);
						hdWgt._origWd = null;
						shallUpdate = true;
					}
				} else if (force || n.offsetWidth != 0) { //hide
					var faker = jq('#' + n.id + '-hdfaker')[0];
					hdWgt._origWd = hdWgt._origWd || faker.style.width;
					cellWidth = '0px';
					shallUpdate = true;
				}
				
				if (force || shallUpdate) {
					if ((faker = jq('#' + n.id + '-hdfaker')[0]))
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-bdfaker')[0]) && isVisible)
						faker.style.width = cellWidth;
					if ((faker = jq('#' + n.id + '-ftfaker')[0]))
						faker.style.width = cellWidth;

					// foot
					if (ftcells)
						ftcells[i].style.width = cellWidth;
					
					var origWd = hdWgt._origWd;
					totalWidth += origWd ? 
							-zk.parseInt(origWd) : zk.parseInt(cellWidth);
				}
			}
		}
		// Set style width to table to avoid colgroup width not working 
		// because of width attribute (width="100%") on table
		if (mesh.eheadtbl)
			mesh.eheadtbl.style.width = jq.px(totalWidth);
		if (mesh.ebodytbl)
			mesh.ebodytbl.style.width = jq.px(totalWidth);
		if (mesh.efoottbl)
			mesh.efoottbl.style.width = jq.px(totalWidth);
		
		mesh._restoreFocus();
		
		// Bug ZK-601, Bug ZK-1572
		if (zk.ie8_ || zk.ie9_)
			zk(mesh).redoCSS();
	}
});

})();
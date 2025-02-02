inherit autotools-brokensep pkgconfig

DESCRIPTION = "The program acquires utility meter readings from wired m-bus or wireless wm-bus meters."
HOMEPAGE = "https://github.com/wmbusmeters/wmbusmeters"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-only;md5=c79ff39f19dfec6d293b95dea7b07891"

# The Makefile is stripping. Overwrite the "STRIP" variable in the Makefile and use the stripping from Yocto
EXTRA_OEMAKE += "STRIP=true"
CXXFLAGS += "-DLIBXML_DOCB_ENABLED"

SRC_URI = " \
    file://0001-set-libxml2-cxxflags-by-pkg-config.patch \
    git://github.com/wmbusmeters/wmbusmeters;protocol=https;branch=master \
"
# Use version 1.18.0
SRCREV = "2f2720d9d8e043304a366d11d933be9d535e17a5"

DEPENDS = "rtlsdr icu libxml2"
RDEPENDS:${PN} = "rtlsdr icu"

S = "${WORKDIR}/git"

do_install () {
	if ("${@'true' if oe.types.boolean(d.getVar('VOLATILE_LOG_DIR')) else 'false'}";) then
		# /var/log is typically a symbolic link to inside /var/volatile,
		# which is expected to be empty.
		rm -rf ${D}${localstatedir}/log
	else
		chown root:systemd-journal ${D}${localstatedir}/log/journal

		# journal-remote creates this at start
		rm -rf ${D}${localstatedir}/log/journal/remote
	fi
}

FILES:${PN} = " etc \
                var \
                lib \
                usr/bin \
                usr/sbin \
                ${systemd_system_unitdir}/system/wmbusmeters.service \
"
SYSTEMD_SERVICE:${PN} = "wmbusmeters.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"